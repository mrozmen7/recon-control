package com.yavuzozmen.reconcontrol.ops.adapter.in.web;

import com.yavuzozmen.reconcontrol.ops.application.OpsLogEntry;

public record OpsLogEntryResponse(
    String timestamp,
    String level,
    String serviceName,
    String correlationId,
    String message
) {
    public static OpsLogEntryResponse fromDomain(OpsLogEntry entry) {
        return new OpsLogEntryResponse(
            entry.timestamp(),
            entry.level(),
            entry.serviceName(),
            entry.correlationId(),
            entry.message()
        );
    }
}
