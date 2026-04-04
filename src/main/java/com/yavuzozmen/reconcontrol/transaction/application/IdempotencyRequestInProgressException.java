package com.yavuzozmen.reconcontrol.transaction.application;

public class IdempotencyRequestInProgressException extends RuntimeException {

    public IdempotencyRequestInProgressException(String idempotencyKey) {
        super("request with idempotency key is still being processed: " + idempotencyKey);
    }
}
