package com.yavuzozmen.reconcontrol.fraud.application;

import com.yavuzozmen.reconcontrol.fraud.domain.FraudMatch;
import com.yavuzozmen.reconcontrol.fraud.domain.FraudSeverity;
import com.yavuzozmen.reconcontrol.infra.kafka.event.TransactionEventPayload;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FraudRuleEngine {

    private final BigDecimal highAmountThreshold;
    private final int burstThreshold;

    public FraudRuleEngine(FraudRulesProperties properties) {
        Objects.requireNonNull(properties, "properties must not be null");
        this.highAmountThreshold = Objects.requireNonNull(
            properties.highAmountThreshold(),
            "highAmountThreshold must not be null"
        );
        this.burstThreshold = properties.burstThreshold();
    }

    public List<FraudMatch> evaluate(TransactionEventPayload event, long recentTransactionCount) {
        Objects.requireNonNull(event, "event must not be null");

        List<FraudMatch> matches = new ArrayList<>();

        if (event.amount().compareTo(highAmountThreshold) >= 0) {
            matches.add(
                new FraudMatch(
                    "HIGH_AMOUNT_TRANSFER",
                    FraudSeverity.HIGH,
                    "Transaction amount exceeded configured threshold of " + highAmountThreshold
                )
            );
        }

        if (recentTransactionCount >= burstThreshold) {
            matches.add(
                new FraudMatch(
                    "RAPID_TRANSFER_BURST",
                    FraudSeverity.MEDIUM,
                    "Account produced %s recent transactions within the burst window"
                        .formatted(recentTransactionCount)
                )
            );
        }

        return List.copyOf(matches);
    }
}
