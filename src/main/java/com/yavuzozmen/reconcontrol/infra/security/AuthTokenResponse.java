package com.yavuzozmen.reconcontrol.infra.security;

import java.time.Instant;
import java.util.List;

public record AuthTokenResponse(
    String accessToken,
    String tokenType,
    Instant expiresAt,
    List<String> roles
) {
}
