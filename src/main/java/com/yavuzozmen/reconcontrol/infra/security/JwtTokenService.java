package com.yavuzozmen.reconcontrol.infra.security;

import java.time.Instant;
import java.util.Objects;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;

public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public JwtTokenService(JwtEncoder jwtEncoder, JwtProperties jwtProperties) {
        this.jwtEncoder = Objects.requireNonNull(jwtEncoder, "jwtEncoder must not be null");
        this.jwtProperties = Objects.requireNonNull(
            jwtProperties,
            "jwtProperties must not be null"
        );
    }

    public AuthTokenResponse issueToken(DemoUserAccount user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(jwtProperties.expiryDuration());

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(jwtProperties.getIssuer())
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .subject(user.username())
            .claim("roles", user.roles())
            .build();

        String token = jwtEncoder.encode(
            JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                claims
            )
        ).getTokenValue();

        return new AuthTokenResponse(token, "Bearer", expiresAt, user.roles());
    }
}
