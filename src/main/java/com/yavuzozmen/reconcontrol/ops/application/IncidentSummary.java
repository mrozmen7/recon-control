package com.yavuzozmen.reconcontrol.ops.application;

import java.util.List;

public record IncidentSummary(
    String mode,
    int lookbackMinutes,
    String summary,
    List<String> keySignals,
    List<OpsMetric> supportingMetrics,
    List<OpsLogEntry> recentLogs,
    List<KnowledgeDocument> referencedDocuments
) {}
