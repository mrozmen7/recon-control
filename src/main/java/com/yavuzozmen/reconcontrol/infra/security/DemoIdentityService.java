package com.yavuzozmen.reconcontrol.infra.security;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DemoIdentityService {

    private final Map<String, DemoUserAccount> usersByUsername;
    private final PasswordEncoder passwordEncoder;

    public DemoIdentityService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = Objects.requireNonNull(
            passwordEncoder,
            "passwordEncoder must not be null"
        );
        this.usersByUsername = Map.of(
            "ops-user",
            new DemoUserAccount(
                "ops-user",
                passwordEncoder.encode("OpsUser123!"),
                List.of("OPS_USER")
            ),
            "ops-admin",
            new DemoUserAccount(
                "ops-admin",
                passwordEncoder.encode("OpsAdmin123!"),
                List.of("OPS_USER", "OPS_ADMIN")
            ),
            "auditor",
            new DemoUserAccount(
                "auditor",
                passwordEncoder.encode("Auditor123!"),
                List.of("AUDITOR")
            )
        );
    }

    public DemoUserAccount authenticate(String username, String rawPassword) {
        DemoUserAccount user = usersByUsername.get(username);
        if (user == null || !passwordEncoder.matches(rawPassword, user.passwordHash())) {
            throw new InvalidCredentialsException();
        }
        return user;
    }
}
