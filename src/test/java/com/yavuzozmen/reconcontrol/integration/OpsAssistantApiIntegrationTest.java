package com.yavuzozmen.reconcontrol.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yavuzozmen.reconcontrol.account.application.GetAccountUseCase;
import com.yavuzozmen.reconcontrol.account.application.OpenAccountUseCase;
import com.yavuzozmen.reconcontrol.ops.application.AnswerOpsQuestionUseCase;
import com.yavuzozmen.reconcontrol.ops.application.CorrelationExplanation;
import com.yavuzozmen.reconcontrol.ops.application.ExplainCorrelationUseCase;
import com.yavuzozmen.reconcontrol.ops.application.IncidentSummary;
import com.yavuzozmen.reconcontrol.ops.application.IncidentSummaryUseCase;
import com.yavuzozmen.reconcontrol.ops.application.KnowledgeDocument;
import com.yavuzozmen.reconcontrol.ops.application.OpsAssistantAnswer;
import com.yavuzozmen.reconcontrol.ops.application.OpsLogEntry;
import com.yavuzozmen.reconcontrol.ops.application.OpsMetric;
import com.yavuzozmen.reconcontrol.transaction.application.CreateInternalTransactionUseCase;
import com.yavuzozmen.reconcontrol.transaction.application.GetInternalTransactionUseCase;
import com.yavuzozmen.reconcontrol.transaction.application.ListInternalTransactionsUseCase;
import com.yavuzozmen.reconcontrol.transaction.application.MarkTransactionSettlementPendingUseCase;
import com.yavuzozmen.reconcontrol.transaction.application.SettleTransactionUseCase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpsAssistantApiIntegrationTest {

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

    @MockBean
    private IncidentSummaryUseCase incidentSummaryUseCase;

    @MockBean
    private ExplainCorrelationUseCase explainCorrelationUseCase;

    @MockBean
    private AnswerOpsQuestionUseCase answerOpsQuestionUseCase;

    @Test
    void shouldReturnIncidentSummaryForAuditor() throws Exception {
        given(incidentSummaryUseCase.handle(any()))
            .willReturn(
                new IncidentSummary(
                    "heuristic",
                    15,
                    "summary",
                    List.of("signal"),
                    List.of(new OpsMetric("app_up", "state", "1")),
                    List.of(new OpsLogEntry("2026-04-04T10:00:00Z", "INFO", "recon-control", "corr-1", "log")),
                    List.of(new KnowledgeDocument("doc", "docs/a.md", "excerpt"))
                )
            );

        mockMvc.perform(
                post("/api/v1/ops/incident-summary")
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AUDITOR")))
                    .contentType("application/json")
                    .content("{\"lookbackMinutes\":15}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mode").value("heuristic"))
            .andExpect(jsonPath("$.summary").value("summary"));
    }

    @Test
    void shouldReturnCorrelationExplanationForAuditor() throws Exception {
        given(explainCorrelationUseCase.handle(eq("corr-1")))
            .willReturn(
                new CorrelationExplanation(
                    "heuristic",
                    "corr-1",
                    "correlation summary",
                    List.of("Matched logs: 1"),
                    List.of(new OpsLogEntry("2026-04-04T10:00:00Z", "INFO", "recon-control", "corr-1", "log"))
                )
            );

        mockMvc.perform(
                get("/api/v1/ops/correlation/corr-1")
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AUDITOR")))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.correlationId").value("corr-1"))
            .andExpect(jsonPath("$.summary").value("correlation summary"));
    }

    @Test
    void shouldReturnOpsAssistantAnswerForOpsAdmin() throws Exception {
        given(answerOpsQuestionUseCase.handle(eq("What happened?"), eq(30)))
            .willReturn(
                new OpsAssistantAnswer(
                    "heuristic",
                    "What happened?",
                    "answer",
                    List.of(new KnowledgeDocument("doc", "docs/a.md", "excerpt")),
                    List.of(new OpsMetric("app_up", "state", "1")),
                    List.of(new OpsLogEntry("2026-04-04T10:00:00Z", "INFO", "recon-control", "corr-1", "log"))
                )
            );

        mockMvc.perform(
                post("/api/v1/ops/assistant")
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPS_ADMIN")))
                    .contentType("application/json")
                    .content("""
                        {
                          "question": "What happened?",
                          "lookbackMinutes": 30
                        }
                        """)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.question").value("What happened?"))
            .andExpect(jsonPath("$.answer").value("answer"));
    }
}
