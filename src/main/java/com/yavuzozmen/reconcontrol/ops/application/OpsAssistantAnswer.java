package com.yavuzozmen.reconcontrol.ops.application;

import java.util.List;

public record OpsAssistantAnswer(
    String mode,
    String question,
    String answer,
    List<KnowledgeDocument> referencedDocuments,
    List<OpsMetric> supportingMetrics,
    List<OpsLogEntry> supportingLogs
) {}
