package com.yavuzozmen.reconcontrol.transaction.adapter.in.web;

import com.yavuzozmen.reconcontrol.common.domain.Money;
import com.yavuzozmen.reconcontrol.transaction.application.CreateInternalTransactionCommand;
import com.yavuzozmen.reconcontrol.transaction.application.CreateInternalTransactionUseCase;
import com.yavuzozmen.reconcontrol.transaction.application.ListInternalTransactionsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Internal banking transaction endpoints")
public class TransactionController {

    private final CreateInternalTransactionUseCase createInternalTransactionUseCase;
    private final ListInternalTransactionsUseCase listInternalTransactionsUseCase;

    public TransactionController(
        CreateInternalTransactionUseCase createInternalTransactionUseCase,
        ListInternalTransactionsUseCase listInternalTransactionsUseCase
    ) {
        this.createInternalTransactionUseCase = Objects.requireNonNull(
            createInternalTransactionUseCase,
            "createInternalTransactionUseCase must not be null"
        );
        this.listInternalTransactionsUseCase = Objects.requireNonNull(
            listInternalTransactionsUseCase,
            "listInternalTransactionsUseCase must not be null"
        );
    }

    @PostMapping
    @Operation(
        summary = "Create an internal transaction",
        description = "Creates a new internal bank-side transaction record for an existing account."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Transaction created"),
        @ApiResponse(
            responseCode = "400",
            description = "Validation or request error",
            content = @Content(schema = @Schema(implementation = com.yavuzozmen.reconcontrol.common.adapter.in.web.ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Account not found",
            content = @Content(schema = @Schema(implementation = com.yavuzozmen.reconcontrol.common.adapter.in.web.ApiErrorResponse.class))
        )
    })
    public ResponseEntity<InternalTransactionResponse> createTransaction(
        @Valid @RequestBody CreateInternalTransactionRequest request
    ) {
        InternalTransactionResponse response = InternalTransactionResponse.fromDomain(
            createInternalTransactionUseCase.handle(
                new CreateInternalTransactionCommand(
                    request.referenceNo(),
                    request.accountId(),
                    request.type(),
                    new Money(request.amount(), request.currency()),
                    request.valueDate()
                )
            )
        );

        return ResponseEntity
            .created(URI.create("/api/v1/transactions/" + response.id()))
            .body(response);
    }

    @GetMapping
    @Operation(
        summary = "List internal transactions",
        description = "Returns all internal transactions, optionally filtered by account id."
    )
    @ApiResponse(responseCode = "200", description = "Transactions returned")
    public List<InternalTransactionResponse> listTransactions(
        @Parameter(description = "Optional account id filter")
        @RequestParam(required = false) UUID accountId
    ) {
        return listInternalTransactionsUseCase.handle(accountId)
            .stream()
            .map(InternalTransactionResponse::fromDomain)
            .toList();
    }
}
