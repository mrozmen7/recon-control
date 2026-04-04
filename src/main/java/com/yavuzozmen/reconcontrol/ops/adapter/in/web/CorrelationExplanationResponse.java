package com.yavuzozmen.reconcontrol.ops.adapter.in.web;

import com.yavuzozmen.reconcontrol.ops.application.CorrelationExplanation;
import java.util.List;

public record CorrelationExplanationResponse(
    String mode,
    String correlationId,
    String summary,
    List<String> keySignals,
    List<OpsLogEntryResponse> logs
) {
    public static CorrelationExplanationResponse fromDomain(CorrelationExplanation explanation) {
        return new CorrelationExplanationResponse(
            explanation.mode(),
            explanation.correlationId(),
            explanation.summary(),
            explanation.keySignals(),
            explanation.logs().stream().map(OpsLogEntryResponse::fromDomain).toList()
        );
    }
}
