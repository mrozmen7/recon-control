package com.yavuzozmen.reconcontrol.transaction.application;

import java.util.UUID;

public record IdempotencyRecord(
    IdempotencyStatus status,
    UUID transactionId
) {
}
