package com.yavuzozmen.reconcontrol.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yavuzozmen.reconcontrol.account.adapter.out.persistence.AccountJpaRepository;
import com.yavuzozmen.reconcontrol.transaction.adapter.out.persistence.InternalTransactionJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(properties = "spring.docker.compose.enabled=false")
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("integration")
class AccountTransactionApiIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("recon_control")
        .withUsername("recon_app")
        .withPassword("recon_secret");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InternalTransactionJpaRepository transactionRepository;

    @Autowired
    private AccountJpaRepository accountRepository;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @BeforeEach
    void cleanState() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        try (var connection = redisConnectionFactory.getConnection()) {
            connection.serverCommands().flushAll();
        }
    }

    @Test
    void shouldAuthenticateCreateReplayReadAndListTransactions() throws Exception {
        String opsUserToken = authenticate("ops-user", "OpsUser123!");
        String auditorToken = authenticate("auditor", "Auditor123!");
        String accountId = createAccount(opsUserToken, "CH3000000003", "cust-003");

        MvcResult createResult = mockMvc.perform(
                post("/api/v1/transactions")
                    .header("Authorization", bearer(opsUserToken))
                    .header("Idempotency-Key", "idem-create-001")
                    .contentType("application/json")
                    .content("""
                        {
                          "referenceNo": "TRX-INT-001",
                          "accountId": "%s",
                          "type": "CREDIT",
                          "amount": 250.75,
                          "currency": "CHF",
                          "valueDate": "2026-03-30"
                        }
                        """.formatted(accountId))
            )
            .andExpect(status().isCreated())
            .andExpect(header().string("X-Idempotent-Replay", "false"))
            .andExpect(jsonPath("$.referenceNo").value("TRX-INT-001"))
            .andExpect(jsonPath("$.status").value("BOOKED"))
            .andReturn();

        String transactionId = readField(createResult, "id");

        mockMvc.perform(
                post("/api/v1/transactions")
                    .header("Authorization", bearer(opsUserToken))
                    .header("Idempotency-Key", "idem-create-001")
                    .contentType("application/json")
                    .content("""
                        {
                          "referenceNo": "TRX-INT-001",
                          "accountId": "%s",
                          "type": "CREDIT",
                          "amount": 250.75,
                          "currency": "CHF",
                          "valueDate": "2026-03-30"
                        }
                        """.formatted(accountId))
            )
            .andExpect(status().isOk())
            .andExpect(header().string("X-Idempotent-Replay", "true"))
            .andExpect(jsonPath("$.id").value(transactionId));

        mockMvc.perform(
                get("/api/v1/accounts/{accountId}", accountId)
                    .header("Authorization", bearer(auditorToken))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance").value(250.75));

        mockMvc.perform(
                get("/api/v1/transactions/{transactionId}", transactionId)
                    .header("Authorization", bearer(auditorToken))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("BOOKED"));

        mockMvc.perform(
                get("/api/v1/transactions")
                    .header("Authorization", bearer(auditorToken))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].referenceNo").value("TRX-INT-001"));

        mockMvc.perform(
                get("/api/v1/transactions").param("accountId", accountId)
                    .header("Authorization", bearer(auditorToken))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].accountId").value(accountId));
    }

    @Test
    void shouldEnforceRoleBasedSettlementLifecycle() throws Exception {
        String opsUserToken = authenticate("ops-user", "OpsUser123!");
        String opsAdminToken = authenticate("ops-admin", "OpsAdmin123!");
        String auditorToken = authenticate("auditor", "Auditor123!");
        String accountId = createAccount(opsUserToken, "CH3000000004", "cust-004");

        MvcResult createResult = mockMvc.perform(
                post("/api/v1/transactions")
                    .header("Authorization", bearer(opsUserToken))
                    .header("Idempotency-Key", "idem-settle-001")
                    .contentType("application/json")
                    .content("""
                        {
                          "referenceNo": "TRX-SET-001",
                          "accountId": "%s",
                          "type": "CREDIT",
                          "amount": 100.00,
                          "currency": "CHF",
                          "valueDate": "2026-03-30"
                        }
                        """.formatted(accountId))
            )
            .andExpect(status().isCreated())
            .andReturn();

        String transactionId = readField(createResult, "id");

        mockMvc.perform(
                post("/api/v1/transactions/{transactionId}/settlement-pending", transactionId)
                    .header("Authorization", bearer(auditorToken))
            )
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.errorCode").value("FORBIDDEN"));

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

        mockMvc.perform(
                get("/api/v1/transactions/{transactionId}", transactionId)
                    .header("Authorization", bearer(auditorToken))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SETTLED"));
    }

    @Test
    void shouldReturnStructuredErrorWhenTransactionAccountDoesNotExist() throws Exception {
        String opsUserToken = authenticate("ops-user", "OpsUser123!");

        mockMvc.perform(
                post("/api/v1/transactions")
                    .header("Authorization", bearer(opsUserToken))
                    .contentType("application/json")
                    .content("""
                        {
                          "referenceNo": "TRX-INT-404",
                          "accountId": "11111111-1111-1111-1111-111111111111",
                          "type": "DEBIT",
                          "amount": 100.00,
                          "currency": "CHF",
                          "valueDate": "2026-03-30"
                        }
                        """)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND"))
            .andExpect(jsonPath("$.path").value("/api/v1/transactions"));
    }

    @Test
    void shouldRateLimitTransactionCreationRequests() throws Exception {
        String opsUserToken = authenticate("ops-user", "OpsUser123!");
        String accountId = createAccount(opsUserToken, "CH3000000005", "cust-005");

        for (int index = 1; index <= 5; index++) {
            mockMvc.perform(
                    post("/api/v1/transactions")
                        .header("Authorization", bearer(opsUserToken))
                        .header("Idempotency-Key", "idem-rate-" + index)
                        .contentType("application/json")
                        .content("""
                            {
                              "referenceNo": "TRX-RATE-%s",
                              "accountId": "%s",
                              "type": "CREDIT",
                              "amount": 10.00,
                              "currency": "CHF",
                              "valueDate": "2026-03-30"
                            }
                            """.formatted(index, accountId))
                )
                .andExpect(status().isCreated());
        }

        mockMvc.perform(
                post("/api/v1/transactions")
                    .header("Authorization", bearer(opsUserToken))
                    .header("Idempotency-Key", "idem-rate-6")
                    .contentType("application/json")
                    .content("""
                        {
                          "referenceNo": "TRX-RATE-6",
                          "accountId": "%s",
                          "type": "CREDIT",
                          "amount": 10.00,
                          "currency": "CHF",
                          "valueDate": "2026-03-30"
                        }
                        """.formatted(accountId))
            )
            .andExpect(status().isTooManyRequests())
            .andExpect(jsonPath("$.errorCode").value("RATE_LIMIT_EXCEEDED"));
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
        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get(fieldName).asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
