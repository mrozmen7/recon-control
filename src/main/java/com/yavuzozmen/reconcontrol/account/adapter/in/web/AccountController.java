package com.yavuzozmen.reconcontrol.account.adapter.in.web;

import com.yavuzozmen.reconcontrol.account.application.GetAccountUseCase;
import com.yavuzozmen.reconcontrol.account.application.OpenAccountCommand;
import com.yavuzozmen.reconcontrol.account.application.OpenAccountUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Objects;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Account management endpoints")
public class AccountController {

    private final OpenAccountUseCase openAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;

    public AccountController(
        OpenAccountUseCase openAccountUseCase,
        GetAccountUseCase getAccountUseCase
    ) {
        this.openAccountUseCase = Objects.requireNonNull(
            openAccountUseCase,
            "openAccountUseCase must not be null"
        );
        this.getAccountUseCase = Objects.requireNonNull(
            getAccountUseCase,
            "getAccountUseCase must not be null"
        );
    }

    @PostMapping
    @Operation(
        summary = "Open a new account",
        description = "Creates a new active bank account with zero opening balance."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Account created"),
        @ApiResponse(
            responseCode = "400",
            description = "Validation or request error",
            content = @Content(schema = @Schema(implementation = com.yavuzozmen.reconcontrol.common.adapter.in.web.ApiErrorResponse.class))
        )
    })
    public ResponseEntity<AccountResponse> openAccount(@Valid @RequestBody OpenAccountRequest request) {
        AccountResponse response = AccountResponse.fromDomain(
            openAccountUseCase.handle(
                new OpenAccountCommand(
                    request.accountNumber(),
                    request.customerId(),
                    request.currency()
                )
            )
        );

        return ResponseEntity
            .created(URI.create("/api/v1/accounts/" + response.id()))
            .body(response);
    }

    @GetMapping("/{accountId}")
    @Operation(
        summary = "Get account by id",
        description = "Returns the current account snapshot for the provided account identifier."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Account returned"),
        @ApiResponse(
            responseCode = "404",
            description = "Account not found",
            content = @Content(schema = @Schema(implementation = com.yavuzozmen.reconcontrol.common.adapter.in.web.ApiErrorResponse.class))
        )
    })
    public AccountResponse getAccount(@PathVariable UUID accountId) {
        return AccountResponse.fromDomain(getAccountUseCase.handle(accountId));
    }
}
