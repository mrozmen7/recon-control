package com.yavuzozmen.reconcontrol.common.adapter.in.web;

import com.yavuzozmen.reconcontrol.account.application.AccountNotFoundException;
import com.yavuzozmen.reconcontrol.infra.ratelimit.RateLimitExceededException;
import com.yavuzozmen.reconcontrol.infra.security.InvalidCredentialsException;
import com.yavuzozmen.reconcontrol.transaction.application.IdempotencyRequestInProgressException;
import com.yavuzozmen.reconcontrol.transaction.application.TransactionNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Hidden
public class GlobalApiExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountNotFound(
        AccountNotFoundException exception,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.NOT_FOUND,
            "ACCOUNT_NOT_FOUND",
            exception.getMessage(),
            request,
            List.of()
        );
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleTransactionNotFound(
        TransactionNotFoundException exception,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.NOT_FOUND,
            "TRANSACTION_NOT_FOUND",
            exception.getMessage(),
            request,
            List.of()
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(
        InvalidCredentialsException exception,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.UNAUTHORIZED,
            "INVALID_CREDENTIALS",
            exception.getMessage(),
            request,
            List.of()
        );
    }

    @ExceptionHandler(IdempotencyRequestInProgressException.class)
    public ResponseEntity<ApiErrorResponse> handleIdempotencyInProgress(
        IdempotencyRequestInProgressException exception,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.CONFLICT,
            "IDEMPOTENCY_IN_PROGRESS",
            exception.getMessage(),
            request,
            List.of()
        );
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleRateLimitExceeded(
        RateLimitExceededException exception,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.TOO_MANY_REQUESTS,
            "RATE_LIMIT_EXCEEDED",
            exception.getMessage(),
            request,
            List.of()
        );
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiErrorResponse> handleOptimisticLockingFailure(
        OptimisticLockingFailureException exception,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.CONFLICT,
            "CONCURRENT_MODIFICATION",
            "Concurrent modification detected. Please retry the request.",
            request,
            List.of()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
        IllegalArgumentException exception,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "BAD_REQUEST",
            exception.getMessage(),
            request,
            List.of()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState(
        IllegalStateException exception,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.CONFLICT,
            "BUSINESS_RULE_VIOLATION",
            exception.getMessage(),
            request,
            List.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
    ) {
        List<ApiFieldError> fieldErrors = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::toApiFieldError)
            .toList();

        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            "Request validation failed",
            request,
            fieldErrors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMessageNotReadable(
        HttpMessageNotReadableException exception,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "MALFORMED_JSON",
            "Request body is malformed or contains invalid values",
            request,
            List.of()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
        MethodArgumentTypeMismatchException exception,
        HttpServletRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiErrorResponse.of(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "TYPE_MISMATCH",
                    "Invalid value for parameter: " + exception.getName(),
                    request.getRequestURI(),
                    List.of()
                )
            );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFound(
        NoResourceFoundException exception,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.NOT_FOUND,
            "RESOURCE_NOT_FOUND",
            exception.getMessage(),
            request,
            List.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
        Exception exception,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_ERROR",
            "Unexpected server error",
            request,
            List.of()
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
        HttpStatus status,
        String errorCode,
        String message,
        HttpServletRequest request,
        List<ApiFieldError> fieldErrors
    ) {
        return ResponseEntity
            .status(status)
            .body(
                ApiErrorResponse.of(
                    status.value(),
                    status.getReasonPhrase(),
                    errorCode,
                    message,
                    request.getRequestURI(),
                    fieldErrors
                )
            );
    }

    private ApiFieldError toApiFieldError(FieldError fieldError) {
        return new ApiFieldError(fieldError.getField(), fieldError.getDefaultMessage());
    }
}
