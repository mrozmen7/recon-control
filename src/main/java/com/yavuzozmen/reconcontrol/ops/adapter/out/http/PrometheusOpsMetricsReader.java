package com.yavuzozmen.reconcontrol.ops.adapter.out.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yavuzozmen.reconcontrol.ops.application.OpsAssistantProperties;
import com.yavuzozmen.reconcontrol.ops.application.OpsMetric;
import com.yavuzozmen.reconcontrol.ops.application.port.out.OpsMetricsReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PrometheusOpsMetricsReader implements OpsMetricsReader {

    private final OpsAssistantProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public PrometheusOpsMetricsReader(
        OpsAssistantProperties properties,
        ObjectMapper objectMapper,
        HttpClient httpClient
    ) {
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient must not be null");
    }

    @Override
    public List<OpsMetric> readOperationalSnapshot() {
        List<OpsMetric> metrics = new ArrayList<>();
        metrics.add(new OpsMetric("app_up", "state", queryScalar("up{job=\"recon-control\"}")));
        metrics.add(
            new OpsMetric(
                "http_request_rate",
                "req/s",
                queryScalar("sum(rate(http_server_requests_seconds_count[1m]))")
            )
        );
        metrics.add(
            new OpsMetric(
                "total_http_requests",
                "requests",
                queryScalar("sum(http_server_requests_seconds_count)")
            )
        );
        metrics.add(
            new OpsMetric(
                "heap_used",
                "bytes",
                queryScalar("sum(jvm_memory_used_bytes{area=\"heap\"})")
            )
        );
        return List.copyOf(metrics);
    }

    private String queryScalar(String expression) {
        try {
            String encoded = URLEncoder.encode(expression, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                    properties.getPrometheusBaseUrl() + "/api/v1/query?query=" + encoded
                ))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
            HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return "unavailable";
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode result = root.path("data").path("result");
            if (!result.isArray() || result.isEmpty()) {
                return "unavailable";
            }

            JsonNode valueNode = result.get(0).path("value");
            if (!valueNode.isArray() || valueNode.size() < 2) {
                return "unavailable";
            }

            return valueNode.get(1).asText("unavailable");
        } catch (Exception ignored) {
            return "unavailable";
        }
    }
}
