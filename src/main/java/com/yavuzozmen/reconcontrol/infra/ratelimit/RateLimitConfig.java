package com.yavuzozmen.reconcontrol.infra.ratelimit;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("!test")
@EnableConfigurationProperties(TransactionRateLimitProperties.class)
public class RateLimitConfig {

    @Bean
    TransactionCreateRateLimiter transactionCreateRateLimiter(
        RateLimitStore rateLimitStore,
        TransactionRateLimitProperties properties
    ) {
        return new TransactionCreateRateLimiter(rateLimitStore, properties);
    }

    @Bean
    TransactionRateLimitInterceptor transactionRateLimitInterceptor(
        TransactionCreateRateLimiter rateLimiter
    ) {
        return new TransactionRateLimitInterceptor(rateLimiter);
    }

    @Bean
    WebMvcConfigurer rateLimitWebMvcConfigurer(
        TransactionRateLimitInterceptor transactionRateLimitInterceptor
    ) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(transactionRateLimitInterceptor);
            }
        };
    }
}
