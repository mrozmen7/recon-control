package com.yavuzozmen.reconcontrol.ops.application;

import com.yavuzozmen.reconcontrol.ops.application.port.out.OpsKnowledgeBase;
import com.yavuzozmen.reconcontrol.ops.application.port.out.OpsLogReader;
import com.yavuzozmen.reconcontrol.ops.application.port.out.OpsMetricsReader;
import java.util.List;
import java.util.Objects;

public class AnswerOpsQuestionUseCase {

    private final OpsAssistantProperties properties;
    private final OpsKnowledgeBase opsKnowledgeBase;
    private final OpsMetricsReader opsMetricsReader;
    private final OpsLogReader opsLogReader;

    public AnswerOpsQuestionUseCase(
        OpsAssistantProperties properties,
        OpsKnowledgeBase opsKnowledgeBase,
        OpsMetricsReader opsMetricsReader,
        OpsLogReader opsLogReader
    ) {
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
        this.opsKnowledgeBase = Objects.requireNonNull(
            opsKnowledgeBase,
            "opsKnowledgeBase must not be null"
        );
        this.opsMetricsReader = Objects.requireNonNull(
            opsMetricsReader,
            "opsMetricsReader must not be null"
        );
        this.opsLogReader = Objects.requireNonNull(opsLogReader, "opsLogReader must not be null");
    }

    public OpsAssistantAnswer handle(String question, Integer requestedLookbackMinutes) {
        Objects.requireNonNull(question, "question must not be null");

        int lookbackMinutes = requestedLookbackMinutes == null || requestedLookbackMinutes <= 0
            ? properties.getDefaultLookbackMinutes()
            : requestedLookbackMinutes;

        List<KnowledgeDocument> docs = opsKnowledgeBase.search(
            question,
            properties.getMaxKnowledgeDocuments()
        );
        List<OpsMetric> metrics = opsMetricsReader.readOperationalSnapshot();
        List<OpsLogEntry> logs = opsLogReader.findRecentLogs(
            lookbackMinutes,
            Math.min(10, properties.getMaxLogLines())
        );

        String docsSummary = docs.isEmpty()
            ? "No matching internal runbook was retrieved."
            : "Top internal references: " + docs.stream()
                .map(KnowledgeDocument::title)
                .toList();

        String answer = """
            Heuristic ops assistant answer: for the question '%s', I reviewed current operational metrics, recent logs from the last %d minutes, and matching project documentation. %s Use the referenced docs for policy and process guidance, and the supporting logs/metrics for live evidence.
            """.formatted(question, lookbackMinutes, docsSummary).trim();

        return new OpsAssistantAnswer(
            properties.getMode(),
            question,
            answer,
            List.copyOf(docs),
            List.copyOf(metrics),
            List.copyOf(logs)
        );
    }
}
