package com.yavuzozmen.reconcontrol.account.application;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(UUID accountId) {
        super("account not found: " + accountId);
    }
}
