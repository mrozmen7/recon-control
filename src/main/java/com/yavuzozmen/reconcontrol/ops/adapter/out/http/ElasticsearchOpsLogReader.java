package com.yavuzozmen.reconcontrol.ops.adapter.out.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yavuzozmen.reconcontrol.ops.application.OpsAssistantProperties;
import com.yavuzozmen.reconcontrol.ops.application.OpsLogEntry;
import com.yavuzozmen.reconcontrol.ops.application.port.out.OpsLogReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ElasticsearchOpsLogReader implements OpsLogReader {

    private final OpsAssistantProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public ElasticsearchOpsLogReader(
        OpsAssistantProperties properties,
        ObjectMapper objectMapper,
        HttpClient httpClient
    ) {
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient must not be null");
    }

    @Override
    public List<OpsLogEntry> findRecentLogs(int lookbackMinutes, int limit) {
        String body = """
            {
              "size": %d,
              "sort": [{"@timestamp": {"order": "desc"}}],
              "query": {
                "bool": {
                  "filter": [
                    {"range": {"@timestamp": {"gte": "now-%dm"}}}
                  ]
                }
              },
              "_source": ["@timestamp", "level", "service.name", "correlationId", "message"]
            }
            """.formatted(limit, lookbackMinutes);
        return search(body);
    }

    @Override
    public List<OpsLogEntry> findByCorrelationId(String correlationId, int limit) {
        String escapedCorrelationId = correlationId.replace("\"", "");
        String body = """
            {
              "size": %d,
              "sort": [{"@timestamp": {"order": "desc"}}],
              "query": {
                "bool": {
                  "filter": [
                    {"term": {"correlationId": "%s"}}
                  ]
                }
              },
              "_source": ["@timestamp", "level", "service.name", "correlationId", "message"]
            }
            """.formatted(limit, escapedCorrelationId);
        return search(body);
    }

    private List<OpsLogEntry> search(String body) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                    properties.getElasticsearchBaseUrl() + "/" + properties.getLogIndexPattern() + "/_search"
                ))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
            HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return List.of();
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode hits = root.path("hits").path("hits");
            List<OpsLogEntry> entries = new ArrayList<>();
            for (JsonNode hit : hits) {
                JsonNode source = hit.path("_source");
                entries.add(
                    new OpsLogEntry(
                        source.path("@timestamp").asText(""),
                        source.path("level").asText(""),
                        source.path("service").path("name").asText(""),
                        source.path("correlationId").asText(""),
                        source.path("message").asText("")
                    )
                );
            }
            return List.copyOf(entries);
        } catch (Exception ignored) {
            return List.of();
        }
    }
}
