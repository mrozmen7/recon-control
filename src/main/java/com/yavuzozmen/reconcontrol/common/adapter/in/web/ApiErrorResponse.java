package com.yavuzozmen.reconcontrol.common.adapter.in.web;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiErrorResponse(
    OffsetDateTime timestamp,
    int status,
    String error,
    String errorCode,
    String message,
    String path,
    List<ApiFieldError> fieldErrors
) {

    public static ApiErrorResponse of(
        int status,
        String error,
        String errorCode,
        String message,
        String path,
        List<ApiFieldError> fieldErrors
    ) {
        return new ApiErrorResponse(
            OffsetDateTime.now(),
            status,
            error,
            errorCode,
            message,
            path,
            fieldErrors == null ? List.of() : List.copyOf(fieldErrors)
        );
    }
}
