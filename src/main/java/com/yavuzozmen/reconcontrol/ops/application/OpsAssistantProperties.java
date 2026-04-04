package com.yavuzozmen.reconcontrol.ops.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.ops-assistant")
public class OpsAssistantProperties {

    private String mode = "heuristic";
    private String elasticsearchBaseUrl = "http://localhost:9200";
    private String prometheusBaseUrl = "http://localhost:9090";
    private String docsRoot = "docs";
    private String logIndexPattern = "filebeat-*";
    private int defaultLookbackMinutes = 15;
    private int maxLogLines = 20;
    private int maxKnowledgeDocuments = 3;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getElasticsearchBaseUrl() {
        return elasticsearchBaseUrl;
    }

    public void setElasticsearchBaseUrl(String elasticsearchBaseUrl) {
        this.elasticsearchBaseUrl = elasticsearchBaseUrl;
    }

    public String getPrometheusBaseUrl() {
        return prometheusBaseUrl;
    }

    public void setPrometheusBaseUrl(String prometheusBaseUrl) {
        this.prometheusBaseUrl = prometheusBaseUrl;
    }

    public String getDocsRoot() {
        return docsRoot;
    }

    public void setDocsRoot(String docsRoot) {
        this.docsRoot = docsRoot;
    }

    public String getLogIndexPattern() {
        return logIndexPattern;
    }

    public void setLogIndexPattern(String logIndexPattern) {
        this.logIndexPattern = logIndexPattern;
    }

    public int getDefaultLookbackMinutes() {
        return defaultLookbackMinutes;
    }

    public void setDefaultLookbackMinutes(int defaultLookbackMinutes) {
        this.defaultLookbackMinutes = defaultLookbackMinutes;
    }

    public int getMaxLogLines() {
        return maxLogLines;
    }

    public void setMaxLogLines(int maxLogLines) {
        this.maxLogLines = maxLogLines;
    }

    public int getMaxKnowledgeDocuments() {
        return maxKnowledgeDocuments;
    }

    public void setMaxKnowledgeDocuments(int maxKnowledgeDocuments) {
        this.maxKnowledgeDocuments = maxKnowledgeDocuments;
    }
}
