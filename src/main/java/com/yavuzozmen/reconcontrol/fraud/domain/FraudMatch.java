package com.yavuzozmen.reconcontrol.fraud.domain;

import java.util.Objects;

public record FraudMatch(String ruleCode, FraudSeverity severity, String reason) {

    public FraudMatch {
        Objects.requireNonNull(ruleCode, "ruleCode must not be null");
        Objects.requireNonNull(severity, "severity must not be null");
        Objects.requireNonNull(reason, "reason must not be null");
    }
}
