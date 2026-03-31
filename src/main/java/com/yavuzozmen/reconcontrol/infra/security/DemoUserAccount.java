package com.yavuzozmen.reconcontrol.infra.security;

import java.util.List;
import java.util.Objects;

public record DemoUserAccount(
    String username,
    String passwordHash,
    List<String> roles
) {

    public DemoUserAccount {
        Objects.requireNonNull(username, "username must not be null");
        Objects.requireNonNull(passwordHash, "passwordHash must not be null");
        roles = List.copyOf(Objects.requireNonNull(roles, "roles must not be null"));
    }
}
