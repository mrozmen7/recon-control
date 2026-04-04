# ADR-005: Advanced Observability Stack

## Status
Accepted

## Context
By the end of Faz 4, the service exposed health and metrics endpoints,
structured logs, and correlation ids. The next gap was tooling depth.

The team wanted to learn and demonstrate how production teams work with:

- metrics dashboards
- centralized log search
- log shipping from containers
- a single local stack that resembles enterprise troubleshooting

## Decision
We extend the stack with:

- Prometheus for metrics scraping
- Grafana for metrics and log dashboards
- Elasticsearch for centralized log indexing
- Kibana for log exploration
- Filebeat for shipping JSON logs from the application volume to
  Elasticsearch

The application keeps writing JSON logs, but in the docker profile it
also writes them to a shared file volume so that Filebeat can ship them.

## Consequences
### Positive
- Metrics and logs become inspectable through real production-style tools.
- Correlation ids in logs become searchable in Kibana.
- Grafana can use both Prometheus and Elasticsearch data sources.

### Trade-offs
- Local runtime becomes heavier and uses more memory.
- Filebeat adds another moving part to the compose topology.
- Elasticsearch startup is slower than the rest of the stack.
