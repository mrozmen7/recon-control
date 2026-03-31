package com.yavuzozmen.reconcontrol.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.yavuzozmen.reconcontrol.infra.security.DemoIdentityService;
import com.yavuzozmen.reconcontrol.infra.security.JwtProperties;
import com.yavuzozmen.reconcontrol.infra.security.JwtTokenService;
import com.yavuzozmen.reconcontrol.infra.security.RestAccessDeniedHandler;
import com.yavuzozmen.reconcontrol.infra.security.RestAuthenticationEntryPoint;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter,
        RestAuthenticationEntryPoint authenticationEntryPoint,
        RestAccessDeniedHandler accessDeniedHandler
    ) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .authorizeHttpRequests(authorize ->
                authorize
                    .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/api/v1/auth/token",
                        "/actuator/health/**",
                        "/actuator/info",
                        "/actuator/prometheus",
                        "/actuator/metrics/**"
                    ).permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/accounts")
                    .hasAnyRole("OPS_USER", "OPS_ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/v1/accounts/**")
                    .hasAnyRole("OPS_USER", "OPS_ADMIN", "AUDITOR")
                    .requestMatchers(HttpMethod.POST, "/api/v1/transactions/*/settlement-pending")
                    .hasRole("OPS_ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/v1/transactions/*/settle")
                    .hasRole("OPS_ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/v1/transactions")
                    .hasAnyRole("OPS_USER", "OPS_ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/v1/transactions/**")
                    .hasAnyRole("OPS_USER", "OPS_ADMIN", "AUDITOR")
                    .requestMatchers(HttpMethod.GET, "/api/v1/fraud/cases/**")
                    .hasAnyRole("OPS_ADMIN", "AUDITOR")
                    .anyRequest()
                    .authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
            ));

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DemoIdentityService demoIdentityService(PasswordEncoder passwordEncoder) {
        return new DemoIdentityService(passwordEncoder);
    }

    @Bean
    JwtTokenService jwtTokenService(JwtEncoder jwtEncoder, JwtProperties jwtProperties) {
        return new JwtTokenService(jwtEncoder, jwtProperties);
    }

    @Bean
    JwtEncoder jwtEncoder(JwtProperties jwtProperties) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(signingKey(jwtProperties)));
    }

    @Bean
    JwtDecoder jwtDecoder(JwtProperties jwtProperties) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(signingKey(jwtProperties))
            .build();

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(
            jwtProperties.getIssuer()
        );
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer));
        return decoder;
    }

    @Bean
    Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> mapRoles(jwt.getClaimAsStringList("roles")));
        return converter;
    }

    @Bean
    RestAuthenticationEntryPoint restAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new RestAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    RestAccessDeniedHandler restAccessDeniedHandler(ObjectMapper objectMapper) {
        return new RestAccessDeniedHandler(objectMapper);
    }

    private Collection<GrantedAuthority> mapRoles(java.util.List<String> roles) {
        if (roles == null) {
            return java.util.List.of();
        }

        return roles.stream()
            .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toUnmodifiableList());
    }

    private SecretKey signingKey(JwtProperties jwtProperties) {
        return new SecretKeySpec(
            jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8),
            "HmacSHA256"
        );
    }
}
