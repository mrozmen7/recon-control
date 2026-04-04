package com.yavuzozmen.reconcontrol.ops.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yavuzozmen.reconcontrol.ops.adapter.out.docs.FilesystemOpsKnowledgeBase;
import com.yavuzozmen.reconcontrol.ops.adapter.out.http.ElasticsearchOpsLogReader;
import com.yavuzozmen.reconcontrol.ops.adapter.out.http.PrometheusOpsMetricsReader;
import com.yavuzozmen.reconcontrol.ops.application.port.out.OpsKnowledgeBase;
import com.yavuzozmen.reconcontrol.ops.application.port.out.OpsLogReader;
import com.yavuzozmen.reconcontrol.ops.application.port.out.OpsMetricsReader;
import java.net.http.HttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OpsAssistantProperties.class)
public class OpsAssistantApplicationConfig {

    @Bean
    HttpClient opsAssistantHttpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    OpsLogReader opsLogReader(
        OpsAssistantProperties properties,
        ObjectMapper objectMapper,
        HttpClient opsAssistantHttpClient
    ) {
        return new ElasticsearchOpsLogReader(properties, objectMapper, opsAssistantHttpClient);
    }

    @Bean
    OpsMetricsReader opsMetricsReader(
        OpsAssistantProperties properties,
        ObjectMapper objectMapper,
        HttpClient opsAssistantHttpClient
    ) {
        return new PrometheusOpsMetricsReader(properties, objectMapper, opsAssistantHttpClient);
    }

    @Bean
    OpsKnowledgeBase opsKnowledgeBase(OpsAssistantProperties properties) {
        return new FilesystemOpsKnowledgeBase(properties);
    }

    @Bean
    IncidentSummaryUseCase incidentSummaryUseCase(
        OpsAssistantProperties properties,
        OpsLogReader opsLogReader,
        OpsMetricsReader opsMetricsReader,
        OpsKnowledgeBase opsKnowledgeBase
    ) {
        return new IncidentSummaryUseCase(
            properties,
            opsLogReader,
            opsMetricsReader,
            opsKnowledgeBase
        );
    }

    @Bean
    ExplainCorrelationUseCase explainCorrelationUseCase(
        OpsAssistantProperties properties,
        OpsLogReader opsLogReader
    ) {
        return new ExplainCorrelationUseCase(properties, opsLogReader);
    }

    @Bean
    AnswerOpsQuestionUseCase answerOpsQuestionUseCase(
        OpsAssistantProperties properties,
        OpsKnowledgeBase opsKnowledgeBase,
        OpsMetricsReader opsMetricsReader,
        OpsLogReader opsLogReader
    ) {
        return new AnswerOpsQuestionUseCase(
            properties,
            opsKnowledgeBase,
            opsMetricsReader,
            opsLogReader
        );
    }
}
