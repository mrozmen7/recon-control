package com.yavuzozmen.reconcontrol.ops.application;

import com.yavuzozmen.reconcontrol.ops.application.port.out.OpsLogReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExplainCorrelationUseCase {

    private final OpsAssistantProperties properties;
    private final OpsLogReader opsLogReader;

    public ExplainCorrelationUseCase(
        OpsAssistantProperties properties,
        OpsLogReader opsLogReader
    ) {
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
        this.opsLogReader = Objects.requireNonNull(opsLogReader, "opsLogReader must not be null");
    }

    public CorrelationExplanation handle(String correlationId) {
        Objects.requireNonNull(correlationId, "correlationId must not be null");

        List<OpsLogEntry> logs = opsLogReader.findByCorrelationId(
            correlationId,
            properties.getMaxLogLines()
        );
        List<String> keySignals = new ArrayList<>();
        keySignals.add("Matched logs: " + logs.size());

        String summary;
        if (logs.isEmpty()) {
            summary = "No logs were found for the provided correlation id.";
        } else {
            OpsLogEntry first = logs.get(logs.size() - 1);
            OpsLogEntry last = logs.get(0);
            keySignals.add("First observed message: " + safeMessage(first.message()));
            keySignals.add("Last observed message: " + safeMessage(last.message()));
            summary = """
                Correlation id %s matched %d log entries. The request or async flow started around %s and most recently emitted '%s'.
                """.formatted(
                correlationId,
                logs.size(),
                first.timestamp(),
                safeMessage(last.message())
            ).trim();
        }

        return new CorrelationExplanation(
            properties.getMode(),
            correlationId,
            summary,
            List.copyOf(keySignals),
            List.copyOf(logs)
        );
    }

    private String safeMessage(String message) {
        if (message == null) {
            return "(empty)";
        }

        return message.length() <= 120 ? message : message.substring(0, 120) + "...";
    }
}
