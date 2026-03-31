package com.yavuzozmen.reconcontrol.infra.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for Swagger and local testing")
public class AuthController {

    private final DemoIdentityService demoIdentityService;
    private final JwtTokenService jwtTokenService;

    public AuthController(
        DemoIdentityService demoIdentityService,
        JwtTokenService jwtTokenService
    ) {
        this.demoIdentityService = demoIdentityService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/token")
    @Operation(
        summary = "Issue JWT token",
        description = "Returns a bearer token for the local demo users."
    )
    @ApiResponse(responseCode = "200", description = "Token issued")
    public AuthTokenResponse issueToken(@Valid @RequestBody AuthTokenRequest request) {
        return jwtTokenService.issueToken(
            demoIdentityService.authenticate(request.username(), request.password())
        );
    }
}
