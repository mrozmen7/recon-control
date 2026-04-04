package com.yavuzozmen.reconcontrol.ops.adapter.in.web;

import com.yavuzozmen.reconcontrol.ops.application.AnswerOpsQuestionUseCase;
import com.yavuzozmen.reconcontrol.ops.application.ExplainCorrelationUseCase;
import com.yavuzozmen.reconcontrol.ops.application.IncidentSummaryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Objects;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ops")
@Tag(name = "Ops Assistant", description = "AI-ready operations assistant endpoints")
@SecurityRequirement(name = "bearerAuth")
public class OpsAssistantController {

    private final IncidentSummaryUseCase incidentSummaryUseCase;
    private final ExplainCorrelationUseCase explainCorrelationUseCase;
    private final AnswerOpsQuestionUseCase answerOpsQuestionUseCase;

    public OpsAssistantController(
        IncidentSummaryUseCase incidentSummaryUseCase,
        ExplainCorrelationUseCase explainCorrelationUseCase,
        AnswerOpsQuestionUseCase answerOpsQuestionUseCase
    ) {
        this.incidentSummaryUseCase = Objects.requireNonNull(
            incidentSummaryUseCase,
            "incidentSummaryUseCase must not be null"
        );
        this.explainCorrelationUseCase = Objects.requireNonNull(
            explainCorrelationUseCase,
            "explainCorrelationUseCase must not be null"
        );
        this.answerOpsQuestionUseCase = Objects.requireNonNull(
            answerOpsQuestionUseCase,
            "answerOpsQuestionUseCase must not be null"
        );
    }

    @PostMapping("/incident-summary")
    @Operation(
        summary = "Summarize recent operational state",
        description = "Returns an AI-ready incident summary built from recent logs, metrics, and runbook docs."
    )
    @ApiResponse(responseCode = "200", description = "Incident summary returned")
    public IncidentSummaryResponse incidentSummary(
        @RequestBody(required = false) IncidentSummaryRequest request
    ) {
        return IncidentSummaryResponse.fromDomain(
            incidentSummaryUseCase.handle(request == null ? null : request.lookbackMinutes())
        );
    }

    @GetMapping("/correlation/{correlationId}")
    @Operation(
        summary = "Explain a correlation id",
        description = "Retrieves logs for a correlation id and produces a compact explanation."
    )
    @ApiResponse(responseCode = "200", description = "Correlation explanation returned")
    public CorrelationExplanationResponse explainCorrelation(
        @PathVariable String correlationId
    ) {
        return CorrelationExplanationResponse.fromDomain(
            explainCorrelationUseCase.handle(correlationId)
        );
    }

    @PostMapping("/assistant")
    @Operation(
        summary = "Ask the ops assistant",
        description = "Answers an operational question using logs, metrics, and internal docs."
    )
    @ApiResponse(responseCode = "200", description = "Ops answer returned")
    public OpsAssistantAnswerResponse ask(
        @Valid @RequestBody OpsAssistantQuestionRequest request
    ) {
        return OpsAssistantAnswerResponse.fromDomain(
            answerOpsQuestionUseCase.handle(request.question(), request.lookbackMinutes())
        );
    }
}
