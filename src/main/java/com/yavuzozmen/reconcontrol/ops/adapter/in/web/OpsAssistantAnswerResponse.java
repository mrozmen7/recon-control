package com.yavuzozmen.reconcontrol.ops.adapter.in.web;

import com.yavuzozmen.reconcontrol.ops.application.OpsAssistantAnswer;
import java.util.List;

public record OpsAssistantAnswerResponse(
    String mode,
    String question,
    String answer,
    List<KnowledgeDocumentResponse> referencedDocuments,
    List<OpsMetricResponse> supportingMetrics,
    List<OpsLogEntryResponse> supportingLogs
) {
    public static OpsAssistantAnswerResponse fromDomain(OpsAssistantAnswer answer) {
        return new OpsAssistantAnswerResponse(
            answer.mode(),
            answer.question(),
            answer.answer(),
            answer.referencedDocuments().stream()
                .map(KnowledgeDocumentResponse::fromDomain)
                .toList(),
            answer.supportingMetrics().stream().map(OpsMetricResponse::fromDomain).toList(),
            answer.supportingLogs().stream().map(OpsLogEntryResponse::fromDomain).toList()
        );
    }
}
