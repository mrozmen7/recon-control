package com.yavuzozmen.reconcontrol.ops.adapter.in.web;

import com.yavuzozmen.reconcontrol.ops.application.IncidentSummary;
import java.util.List;

public record IncidentSummaryResponse(
    String mode,
    int lookbackMinutes,
    String summary,
    List<String> keySignals,
    List<OpsMetricResponse> supportingMetrics,
    List<OpsLogEntryResponse> recentLogs,
    List<KnowledgeDocumentResponse> referencedDocuments
) {
    public static IncidentSummaryResponse fromDomain(IncidentSummary summary) {
        return new IncidentSummaryResponse(
            summary.mode(),
            summary.lookbackMinutes(),
            summary.summary(),
            summary.keySignals(),
            summary.supportingMetrics().stream().map(OpsMetricResponse::fromDomain).toList(),
            summary.recentLogs().stream().map(OpsLogEntryResponse::fromDomain).toList(),
            summary.referencedDocuments().stream()
                .map(KnowledgeDocumentResponse::fromDomain)
                .toList()
        );
    }
}
