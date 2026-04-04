package com.yavuzozmen.reconcontrol.ops.adapter.in.web;

import com.yavuzozmen.reconcontrol.ops.application.OpsMetric;

public record OpsMetricResponse(
    String name,
    String unit,
    String value
) {
    public static OpsMetricResponse fromDomain(OpsMetric metric) {
        return new OpsMetricResponse(metric.name(), metric.unit(), metric.value());
    }
}
