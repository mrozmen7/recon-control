package com.yavuzozmen.reconcontrol.fraud.application;

import java.math.BigDecimal;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.fraud.rules")
public record FraudRulesProperties(
    BigDecimal highAmountThreshold,
    int burstThreshold,
    int burstWindowMinutes
) {}
