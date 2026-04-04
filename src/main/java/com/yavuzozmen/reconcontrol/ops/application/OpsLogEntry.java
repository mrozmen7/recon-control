package com.yavuzozmen.reconcontrol.ops.application;

public record OpsLogEntry(
    String timestamp,
    String level,
    String serviceName,
    String correlationId,
    String message
) {}
