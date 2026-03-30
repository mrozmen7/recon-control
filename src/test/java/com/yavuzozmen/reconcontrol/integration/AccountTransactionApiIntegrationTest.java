package com.yavuzozmen.reconcontrol.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

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

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InternalTransactionJpaRepository transactionRepository;

    @Autowired
    private AccountJpaRepository accountRepository;

    @BeforeEach
    void cleanDatabase() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void shouldCreateReadAndListAccountTransactions() throws Exception {
        String accountId = createAccount("CH3000000003", "cust-003");

        mockMvc.perform(get("/api/v1/accounts/{accountId}", accountId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(accountId))
            .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(
                post("/api/v1/transactions")
                    .contentType("application/json")
                    .content("""
                        {
                          "referenceNo": "TRX-INT-001",
                          "accountId": "%s",
                          "type": "DEBIT",
                          "amount": 250.75,
                          "currency": "CHF",
                          "valueDate": "2026-03-30"
                        }
                        """.formatted(accountId))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.referenceNo").value("TRX-INT-001"))
            .andExpect(jsonPath("$.status").value("RECEIVED"));

        mockMvc.perform(get("/api/v1/transactions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].referenceNo").value("TRX-INT-001"));

        mockMvc.perform(get("/api/v1/transactions").param("accountId", accountId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].accountId").value(accountId));
    }

    @Test
    void shouldReturnStructuredErrorWhenTransactionAccountDoesNotExist() throws Exception {
        mockMvc.perform(
                post("/api/v1/transactions")
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

    private String createAccount(String accountNumber, String customerId) throws Exception {
        MvcResult result = mockMvc.perform(
                post("/api/v1/accounts")
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

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get("id").asText();
    }
}
