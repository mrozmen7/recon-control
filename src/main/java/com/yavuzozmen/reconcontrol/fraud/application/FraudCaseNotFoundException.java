package com.yavuzozmen.reconcontrol.fraud.application;

import java.util.UUID;

public class FraudCaseNotFoundException extends RuntimeException {

    public FraudCaseNotFoundException(UUID fraudCaseId) {
        super("fraud case not found: " + fraudCaseId);
    }
}
