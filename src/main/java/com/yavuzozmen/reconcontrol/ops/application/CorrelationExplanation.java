package com.yavuzozmen.reconcontrol.ops.application;

import java.util.List;

public record CorrelationExplanation(
    String mode,
    String correlationId,
    String summary,
    List<String> keySignals,
    List<OpsLogEntry> logs
) {}
