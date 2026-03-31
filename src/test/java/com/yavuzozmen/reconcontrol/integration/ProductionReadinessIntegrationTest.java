package com.yavuzozmen.reconcontrol.integration;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yavuzozmen.reconcontrol.account.application.GetAccountUseCase;
import com.yavuzozmen.reconcontrol.account.application.OpenAccountUseCase;
import com.yavuzozmen.reconcontrol.transaction.application.CreateInternalTransactionUseCase;
import com.yavuzozmen.reconcontrol.transaction.application.GetInternalTransactionUseCase;
import com.yavuzozmen.reconcontrol.transaction.application.ListInternalTransactionsUseCase;
import com.yavuzozmen.reconcontrol.transaction.application.MarkTransactionSettlementPendingUseCase;
import com.yavuzozmen.reconcontrol.transaction.application.SettleTransactionUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductionReadinessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpenAccountUseCase openAccountUseCase;

    @MockBean
    private GetAccountUseCase getAccountUseCase;

    @MockBean
    private CreateInternalTransactionUseCase createInternalTransactionUseCase;

    @MockBean
    private ListInternalTransactionsUseCase listInternalTransactionsUseCase;

    @MockBean
    private GetInternalTransactionUseCase getInternalTransactionUseCase;

    @MockBean
    private MarkTransactionSettlementPendingUseCase markTransactionSettlementPendingUseCase;

    @MockBean
    private SettleTransactionUseCase settleTransactionUseCase;

    @Test
    void shouldExposeHealthEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(header().exists("X-Correlation-Id"));
    }

    @Test
    void shouldExposeMetricsEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.names").isArray())
            .andExpect(header().exists("X-Correlation-Id"));
    }

    @Test
    void shouldPropagateProvidedCorrelationId() throws Exception {
        mockMvc.perform(post("/api/v1/auth/token")
                .header("X-Correlation-Id", "corr-faz4-001")
                .contentType("application/json")
                .content("""
                    {
                      "username": "ops-user",
                      "password": "OpsUser123!"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(header().string("X-Correlation-Id", "corr-faz4-001"));
    }
}
