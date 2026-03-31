package com.yavuzozmen.reconcontrol.fraud.adapter.in.web;

import com.yavuzozmen.reconcontrol.fraud.application.GetFraudCaseUseCase;
import com.yavuzozmen.reconcontrol.fraud.application.ListFraudCasesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("!test")
@RequestMapping("/api/v1/fraud/cases")
@Tag(name = "Fraud Cases", description = "Fraud monitoring endpoints")
@SecurityRequirement(name = "bearerAuth")
public class FraudCaseController {

    private final ListFraudCasesUseCase listFraudCasesUseCase;
    private final GetFraudCaseUseCase getFraudCaseUseCase;

    public FraudCaseController(
        ListFraudCasesUseCase listFraudCasesUseCase,
        GetFraudCaseUseCase getFraudCaseUseCase
    ) {
        this.listFraudCasesUseCase = Objects.requireNonNull(
            listFraudCasesUseCase,
            "listFraudCasesUseCase must not be null"
        );
        this.getFraudCaseUseCase = Objects.requireNonNull(
            getFraudCaseUseCase,
            "getFraudCaseUseCase must not be null"
        );
    }

    @GetMapping
    @Operation(
        summary = "List fraud cases",
        description = "Returns fraud cases, optionally filtered by transaction id."
    )
    public List<FraudCaseResponse> listFraudCases(
        @Parameter(description = "Optional transaction id filter")
        @RequestParam(required = false) UUID transactionId
    ) {
        return listFraudCasesUseCase.handle(transactionId)
            .stream()
            .map(FraudCaseResponse::fromView)
            .toList();
    }

    @GetMapping("/{fraudCaseId}")
    @Operation(
        summary = "Get fraud case by id",
        description = "Returns a single fraud case."
    )
    public FraudCaseResponse getFraudCase(@PathVariable UUID fraudCaseId) {
        return FraudCaseResponse.fromView(getFraudCaseUseCase.handle(fraudCaseId));
    }
}
