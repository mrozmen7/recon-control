package com.yavuzozmen.reconcontrol.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yavuzozmen.reconcontrol.account.adapter.out.persistence.AccountJpaRepository;
import com.yavuzozmen.reconcontrol.fraud.adapter.out.persistence.FraudCaseJpaRepository;
import com.yavuzozmen.reconcontrol.infra.kafka.KafkaTopicsProperties;
import com.yavuzozmen.reconcontrol.infra.kafka.event.FraudAlertEventPayload;
import com.yavuzozmen.reconcontrol.infra.kafka.event.TransactionEventPayload;
import com.yavuzozmen.reconcontrol.infra.kafka.event.TransactionEventType;
import com.yavuzozmen.reconcontrol.infra.outbox.OutboxEventJpaRepository;
import com.yavuzozmen.reconcontrol.infra.outbox.ProcessedKafkaMessageJpaRepository;
import com.yavuzozmen.reconcontrol.transaction.adapter.out.persistence.InternalTransactionJpaRepository;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(properties = "spring.docker.compose.enabled=false")
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("integration")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EventDrivenFlowIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("recon_control")
        .withUsername("recon_app")
        .withPassword("recon_secret");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
        .withExposedPorts(6379);

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"));

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("app.outbox.publisher.delay-ms", () -> 100L);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaTopicsProperties topicsProperties;

    @Autowired
    private InternalTransactionJpaRepository transactionRepository;

    @Autowired
    private AccountJpaRepository accountRepository;

    @Autowired
    private FraudCaseJpaRepository fraudCaseRepository;

    @Autowired
    private OutboxEventJpaRepository outboxEventJpaRepository;

    @Autowired
    private ProcessedKafkaMessageJpaRepository processedKafkaMessageJpaRepository;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @BeforeEach
    void cleanState() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        fraudCaseRepository.deleteAll();
        outboxEventJpaRepository.deleteAll();
        processedKafkaMessageJpaRepository.deleteAll();
        try (var connection = redisConnectionFactory.getConnection()) {
            connection.serverCommands().flushAll();
        }
    }

    @Test
    void shouldPublishBookedEventCreateFraudCaseAndEmitFraudAlert() throws Exception {
        String opsUserToken = authenticate("ops-user", "OpsUser123!");
        String auditorToken = authenticate("auditor", "Auditor123!");
        String accountId = createAccount(opsUserToken, "CH4000000006", "cust-006");

        MvcResult createResult = mockMvc.perform(
                post("/api/v1/transactions")
                    .header("Authorization", bearer(opsUserToken))
                    .header("Idempotency-Key", "idem-faz3-001")
                    .contentType("application/json")
                    .content("""
                        {
                          "referenceNo": "FAZ3-HIGH-001",
                          "accountId": "%s",
                          "type": "CREDIT",
                          "amount": 5000.00,
                          "currency": "CHF",
                          "valueDate": "2026-03-31"
                        }
                        """.formatted(accountId))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("BOOKED"))
            .andReturn();

        String transactionId = readField(createResult, "id");

        TransactionEventPayload bookedEvent = consumeTransactionEvent(
            event -> event.referenceNo().equals("FAZ3-HIGH-001")
                && event.eventType() == TransactionEventType.TRANSACTION_BOOKED
        );

        assertThat(bookedEvent.transactionId()).hasToString(transactionId);

        Awaitility.await()
            .atMost(Duration.ofSeconds(10))
            .untilAsserted(() -> assertThat(fraudCaseRepository.findAll()).hasSize(1));

        mockMvc.perform(
                get("/api/v1/fraud/cases")
                    .header("Authorization", bearer(auditorToken))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].referenceNo").value("FAZ3-HIGH-001"))
            .andExpect(jsonPath("$[0].ruleCode").value("HIGH_AMOUNT_TRANSFER"));

        FraudAlertEventPayload fraudAlert = consumeFraudAlert(
            alert -> alert.referenceNo().equals("FAZ3-HIGH-001")
                && alert.ruleCode().equals("HIGH_AMOUNT_TRANSFER")
        );

        assertThat(fraudAlert.transactionId()).hasToString(transactionId);
    }

    @Test
    void shouldPublishSettlementLifecycleEvents() throws Exception {
        String opsUserToken = authenticate("ops-user", "OpsUser123!");
        String opsAdminToken = authenticate("ops-admin", "OpsAdmin123!");
        String accountId = createAccount(opsUserToken, "CH4000000007", "cust-007");

        MvcResult createResult = mockMvc.perform(
                post("/api/v1/transactions")
                    .header("Authorization", bearer(opsUserToken))
                    .header("Idempotency-Key", "idem-faz3-002")
                    .contentType("application/json")
                    .content("""
                        {
                          "referenceNo": "FAZ3-SET-001",
                          "accountId": "%s",
                          "type": "CREDIT",
                          "amount": 120.00,
                          "currency": "CHF",
                          "valueDate": "2026-03-31"
                        }
                        """.formatted(accountId))
            )
            .andExpect(status().isCreated())
            .andReturn();

        String transactionId = readField(createResult, "id");

        mockMvc.perform(
                post("/api/v1/transactions/{transactionId}/settlement-pending", transactionId)
                    .header("Authorization", bearer(opsAdminToken))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SETTLEMENT_PENDING"));

        mockMvc.perform(
                post("/api/v1/transactions/{transactionId}/settle", transactionId)
                    .header("Authorization", bearer(opsAdminToken))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SETTLED"));

        TransactionEventPayload pendingEvent = consumeTransactionEvent(
            event -> event.referenceNo().equals("FAZ3-SET-001")
                && event.eventType() == TransactionEventType.TRANSACTION_SETTLEMENT_PENDING
        );
        TransactionEventPayload settledEvent = consumeTransactionEvent(
            event -> event.referenceNo().equals("FAZ3-SET-001")
                && event.eventType() == TransactionEventType.TRANSACTION_SETTLED
        );

        assertThat(pendingEvent.status()).isEqualTo("SETTLEMENT_PENDING");
        assertThat(settledEvent.status()).isEqualTo("SETTLED");
    }

    @Test
    void shouldIgnoreDuplicateTransactionEventsDuringFraudEvaluation() throws Exception {
        String opsUserToken = authenticate("ops-user", "OpsUser123!");
        String accountId = createAccount(opsUserToken, "CH4000000008", "cust-008");

        mockMvc.perform(
                post("/api/v1/transactions")
                    .header("Authorization", bearer(opsUserToken))
                    .header("Idempotency-Key", "idem-faz3-003")
                    .contentType("application/json")
                    .content("""
                        {
                          "referenceNo": "FAZ3-DUP-001",
                          "accountId": "%s",
                          "type": "CREDIT",
                          "amount": 6000.00,
                          "currency": "CHF",
                          "valueDate": "2026-03-31"
                        }
                        """.formatted(accountId))
            )
            .andExpect(status().isCreated());

        TransactionEventPayload originalEvent = consumeTransactionEvent(
            event -> event.referenceNo().equals("FAZ3-DUP-001")
                && event.eventType() == TransactionEventType.TRANSACTION_BOOKED
        );

        Awaitility.await()
            .atMost(Duration.ofSeconds(10))
            .untilAsserted(() -> assertThat(fraudCaseRepository.findAll()).hasSize(1));

        kafkaTemplate.send(
            topicsProperties.transactionEvents(),
            originalEvent.transactionId().toString(),
            objectMapper.writeValueAsString(originalEvent)
        ).get();

        Awaitility.await()
            .during(Duration.ofSeconds(2))
            .atMost(Duration.ofSeconds(4))
            .untilAsserted(() -> assertThat(fraudCaseRepository.findAll()).hasSize(1));
    }

    @Test
    void shouldRouteMalformedTransactionEventToDlt() throws Exception {
        kafkaTemplate.send(
            topicsProperties.transactionEvents(),
            UUID.randomUUID().toString(),
            "{malformed-json"
        ).get();

        String dltPayload = consumeRawMessage(
            topicsProperties.transactionEventsDlt(),
            payload -> payload.equals("{malformed-json")
        );

        assertThat(dltPayload).isEqualTo("{malformed-json");
    }

    private String authenticate(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(
                post("/api/v1/auth/token")
                    .contentType("application/json")
                    .content("""
                        {
                          "username": "%s",
                          "password": "%s"
                        }
                        """.formatted(username, password))
            )
            .andExpect(status().isOk())
            .andReturn();

        return readField(result, "accessToken");
    }

    private String createAccount(String token, String accountNumber, String customerId)
        throws Exception {
        MvcResult result = mockMvc.perform(
                post("/api/v1/accounts")
                    .header("Authorization", bearer(token))
                    .contentType("application/json")
                    .content("""
                        {
                          "accountNumber": "%s",
                          "customerId": "%s",
                          "currency": "CHF"
                        }
                        """.formatted(accountNumber, customerId))
            )
            .andExpect(status().isCreated())
            .andReturn();

        return readField(result, "id");
    }

    private String readField(MvcResult result, String fieldName) throws Exception {
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get(fieldName).asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private TransactionEventPayload consumeTransactionEvent(EventMatcher<TransactionEventPayload> matcher)
        throws Exception {
        String payload = consumeRawMessage(topicsProperties.transactionEvents(), raw -> {
            try {
                return matcher.matches(objectMapper.readValue(raw, TransactionEventPayload.class));
            } catch (Exception exception) {
                return false;
            }
        });
        return objectMapper.readValue(payload, TransactionEventPayload.class);
    }

    private FraudAlertEventPayload consumeFraudAlert(EventMatcher<FraudAlertEventPayload> matcher)
        throws Exception {
        String payload = consumeRawMessage(topicsProperties.fraudAlertEvents(), raw -> {
            try {
                return matcher.matches(objectMapper.readValue(raw, FraudAlertEventPayload.class));
            } catch (Exception exception) {
                return false;
            }
        });
        return objectMapper.readValue(payload, FraudAlertEventPayload.class);
    }

    private String consumeRawMessage(String topic, EventMatcher<String> matcher) {
        try (Consumer<String, String> consumer = new KafkaConsumer<>(consumerProps())) {
            consumer.subscribe(java.util.List.of(topic));

            AtomicReference<String> foundPayload = new AtomicReference<>();
            Awaitility.await()
                .atMost(Duration.ofSeconds(15))
                .until(() -> {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                    for (ConsumerRecord<String, String> record : records) {
                        if (matcher.matches(record.value())) {
                            foundPayload.set(record.value());
                            return true;
                        }
                    }
                    return false;
                });
            return foundPayload.get();
        }
    }

    private Properties consumerProps() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "faz3-test-" + UUID.randomUUID());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return properties;
    }

    @FunctionalInterface
    private interface EventMatcher<T> {
        boolean matches(T value) throws Exception;
    }
}
