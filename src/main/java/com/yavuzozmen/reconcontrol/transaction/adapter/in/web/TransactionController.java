package com.yavuzozmen.reconcontrol.transaction.adapter.in.web;

import com.yavuzozmen.reconcontrol.common.domain.Money;
import com.yavuzozmen.reconcontrol.transaction.application.CreateInternalTransactionCommand;
import com.yavuzozmen.reconcontrol.transaction.application.GetInternalTransactionUseCase;
import com.yavuzozmen.reconcontrol.transaction.application.MarkTransactionSettlementPendingUseCase;
import com.yavuzozmen.reconcontrol.transaction.application.SettleTransactionUseCase;
import com.yavuzozmen.reconcontrol.transaction.application.TransactionCreationResult;
import com.yavuzozmen.reconcontrol.transaction.application.CreateInternalTransactionUseCase;
import com.yavuzozmen.reconcontrol.transaction.application.ListInternalTransactionsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Internal banking transaction endpoints")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final CreateInternalTransactionUseCase createInternalTransactionUseCase;
    private final ListInternalTransactionsUseCase listInternalTransactionsUseCase;
    private final GetInternalTransactionUseCase getInternalTransactionUseCase;
    private final MarkTransactionSettlementPendingUseCase markTransactionSettlementPendingUseCase;
    private final SettleTransactionUseCase settleTransactionUseCase;

    public TransactionController(
        CreateInternalTransactionUseCase createInternalTransactionUseCase,
        ListInternalTransactionsUseCase listInternalTransactionsUseCase,
        GetInternalTransactionUseCase getInternalTransactionUseCase,
        MarkTransactionSettlementPendingUseCase markTransactionSettlementPendingUseCase,
        SettleTransactionUseCase settleTransactionUseCase
    ) {
        this.createInternalTransactionUseCase = Objects.requireNonNull(
            createInternalTransactionUseCase,
            "createInternalTransactionUseCase must not be null"
        );
        this.listInternalTransactionsUseCase = Objects.requireNonNull(
            listInternalTransactionsUseCase,
            "listInternalTransactionsUseCase must not be null"
        );
        this.getInternalTransactionUseCase = Objects.requireNonNull(
            getInternalTransactionUseCase,
            "getInternalTransactionUseCase must not be null"
        );
        this.markTransactionSettlementPendingUseCase = Objects.requireNonNull(
            markTransactionSettlementPendingUseCase,
            "markTransactionSettlementPendingUseCase must not be null"
        );
        this.settleTransactionUseCase = Objects.requireNonNull(
            settleTransactionUseCase,
            "settleTransactionUseCase must not be null"
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
        @Parameter(
            description = "Optional idempotency key to safely replay the same create request"
        )
        @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
        @Valid @RequestBody CreateInternalTransactionRequest request
    ) {
        TransactionCreationResult result = createInternalTransactionUseCase.handle(
            new CreateInternalTransactionCommand(
                request.referenceNo(),
                request.accountId(),
                request.type(),
                new Money(request.amount(), request.currency()),
                request.valueDate(),
                idempotencyKey
            )
        );

        InternalTransactionResponse response = InternalTransactionResponse.fromDomain(
            result.transaction()
        );

        ResponseEntity.BodyBuilder builder = result.replayed()
            ? ResponseEntity.ok()
            : ResponseEntity.created(URI.create("/api/v1/transactions/" + response.id()));

        return builder
            .header(HttpHeaders.LOCATION, "/api/v1/transactions/" + response.id())
            .header("X-Idempotent-Replay", String.valueOf(result.replayed()))
            .body(response);
    }

    @GetMapping("/{transactionId}")
    @Operation(
        summary = "Get internal transaction by id",
        description = "Returns the current transaction snapshot for the provided transaction identifier."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaction returned"),
        @ApiResponse(
            responseCode = "404",
            description = "Transaction not found",
            content = @Content(schema = @Schema(implementation = com.yavuzozmen.reconcontrol.common.adapter.in.web.ApiErrorResponse.class))
        )
    })
    public InternalTransactionResponse getTransaction(@PathVariable UUID transactionId) {
        return InternalTransactionResponse.fromDomain(
            getInternalTransactionUseCase.handle(transactionId)
        );
    }

    @PostMapping("/{transactionId}/settlement-pending")
    @Operation(
        summary = "Mark transaction as settlement pending",
        description = "Moves a booked transaction into settlement pending state."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaction state updated"),
        @ApiResponse(
            responseCode = "404",
            description = "Transaction not found",
            content = @Content(schema = @Schema(implementation = com.yavuzozmen.reconcontrol.common.adapter.in.web.ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Invalid state transition",
            content = @Content(schema = @Schema(implementation = com.yavuzozmen.reconcontrol.common.adapter.in.web.ApiErrorResponse.class))
        )
    })
    public InternalTransactionResponse markSettlementPending(@PathVariable UUID transactionId) {
        return InternalTransactionResponse.fromDomain(
            markTransactionSettlementPendingUseCase.handle(transactionId)
        );
    }

    @PostMapping("/{transactionId}/settle")
    @Operation(
        summary = "Settle transaction",
        description = "Marks a settlement-pending transaction as settled."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaction settled"),
        @ApiResponse(
            responseCode = "404",
            description = "Transaction not found",
            content = @Content(schema = @Schema(implementation = com.yavuzozmen.reconcontrol.common.adapter.in.web.ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Invalid state transition",
            content = @Content(schema = @Schema(implementation = com.yavuzozmen.reconcontrol.common.adapter.in.web.ApiErrorResponse.class))
        )
    })
    public InternalTransactionResponse settleTransaction(@PathVariable UUID transactionId) {
        return InternalTransactionResponse.fromDomain(
            settleTransactionUseCase.handle(transactionId)
        );
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
