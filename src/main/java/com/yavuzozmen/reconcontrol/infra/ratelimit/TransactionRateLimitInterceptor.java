package com.yavuzozmen.reconcontrol.infra.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

public class TransactionRateLimitInterceptor implements HandlerInterceptor {

    private final TransactionCreateRateLimiter rateLimiter;

    public TransactionRateLimitInterceptor(TransactionCreateRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) {
        if ("POST".equalsIgnoreCase(request.getMethod())
            && "/api/v1/transactions".equals(request.getRequestURI())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String actor = authentication == null ? request.getRemoteAddr() : authentication.getName();
            rateLimiter.checkLimit(actor);
        }

        return true;
    }
}
