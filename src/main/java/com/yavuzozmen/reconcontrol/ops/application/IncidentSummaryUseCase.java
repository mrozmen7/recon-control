package com.yavuzozmen.reconcontrol.ops.application;

import com.yavuzozmen.reconcontrol.ops.application.port.out.OpsKnowledgeBase;
import com.yavuzozmen.reconcontrol.ops.application.port.out.OpsLogReader;
import com.yavuzozmen.reconcontrol.ops.application.port.out.OpsMetricsReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IncidentSummaryUseCase {

    private final OpsAssistantProperties properties;
    private final OpsLogReader opsLogReader;
    private final OpsMetricsReader opsMetricsReader;
    private final OpsKnowledgeBase opsKnowledgeBase;

    public IncidentSummaryUseCase(
        OpsAssistantProperties properties,
        OpsLogReader opsLogReader,
        OpsMetricsReader opsMetricsReader,
        OpsKnowledgeBase opsKnowledgeBase
    ) {
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
        this.opsLogReader = Objects.requireNonNull(opsLogReader, "opsLogReader must not be null");
        this.opsMetricsReader = Objects.requireNonNull(
            opsMetricsReader,
            "opsMetricsReader must not be null"
        );
        this.opsKnowledgeBase = Objects.requireNonNull(
            opsKnowledgeBase,
            "opsKnowledgeBase must not be null"
        );
    }

    public IncidentSummary handle(Integer requestedLookbackMinutes) {
        int lookbackMinutes = normalizeLookbackMinutes(requestedLookbackMinutes);
        List<OpsMetric> metrics = opsMetricsReader.readOperationalSnapshot();
        List<OpsLogEntry> logs = opsLogReader.findRecentLogs(
            lookbackMinutes,
            properties.getMaxLogLines()
        );
        List<KnowledgeDocument> docs = opsKnowledgeBase.search(
            "incident summary observability runbook metrics logs",
            properties.getMaxKnowledgeDocuments()
        );

        List<String> keySignals = new ArrayList<>();
        metrics.stream()
            .map(metric -> "%s: %s %s".formatted(
                metric.name(),
                metric.value(),
                metric.unit()
            ).trim())
            .forEach(keySignals::add);

        long warnCount = logs.stream()
            .filter(log -> "WARN".equalsIgnoreCase(log.level()))
            .count();
        long errorCount = logs.stream()
            .filter(log -> "ERROR".equalsIgnoreCase(log.level()))
            .count();

        keySignals.add("Recent WARN logs: " + warnCount);
        keySignals.add("Recent ERROR logs: " + errorCount);

        String summary = """
            Heuristic incident summary for the last %d minutes: app telemetry is reachable, recent traffic and heap metrics were collected successfully, and %d recent logs were analyzed. WARN=%d and ERROR=%d in the inspected window. Use the attached logs and referenced runbooks for drill-down.
            """.formatted(lookbackMinutes, logs.size(), warnCount, errorCount).trim();

        return new IncidentSummary(
            properties.getMode(),
            lookbackMinutes,
            summary,
            List.copyOf(keySignals),
            List.copyOf(metrics),
            List.copyOf(logs),
            List.copyOf(docs)
        );
    }

    private int normalizeLookbackMinutes(Integer requestedLookbackMinutes) {
        if (requestedLookbackMinutes == null || requestedLookbackMinutes <= 0) {
            return properties.getDefaultLookbackMinutes();
        }

        return requestedLookbackMinutes;
    }
}
