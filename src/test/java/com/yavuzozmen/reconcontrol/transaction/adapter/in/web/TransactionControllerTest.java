package com.yavuzozmen.reconcontrol.transaction.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yavuzozmen.reconcontrol.common.adapter.in.web.GlobalApiExceptionHandler;
import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;
import com.yavuzozmen.reconcontrol.common.domain.Money;
import com.yavuzozmen.reconcontrol.infra.SecurityConfig;
import com.yavuzozmen.reconcontrol.transaction.application.CreateInternalTransactionUseCase;
import com.yavuzozmen.reconcontrol.transaction.application.ListInternalTransactionsUseCase;
import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import com.yavuzozmen.reconcontrol.transaction.domain.TransactionStatus;
import com.yavuzozmen.reconcontrol.transaction.domain.TransactionType;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TransactionController.class)
@Import({GlobalApiExceptionHandler.class, SecurityConfig.class})
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateInternalTransactionUseCase createInternalTransactionUseCase;

    @MockBean
    private ListInternalTransactionsUseCase listInternalTransactionsUseCase;

    @Test
    void shouldCreateInternalTransaction() throws Exception {
        InternalTransaction transaction = InternalTransaction.rehydrate(
            UUID.fromString("44444444-4444-4444-4444-444444444444"),
            "TRX-0001",
            UUID.fromString("aac42efa-41fc-4e6d-8a5f-36040ff924f9"),
            TransactionType.DEBIT,
            new Money(new java.math.BigDecimal("125.5000"), CurrencyCode.CHF),
            LocalDate.parse("2026-03-30"),
            OffsetDateTime.parse("2026-03-30T19:00:00+02:00"),
            TransactionStatus.RECEIVED
        );
        given(createInternalTransactionUseCase.handle(any())).willReturn(transaction);

        mockMvc.perform(
                post("/api/v1/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "referenceNo": "TRX-0001",
                          "accountId": "aac42efa-41fc-4e6d-8a5f-36040ff924f9",
                          "type": "DEBIT",
                          "amount": 125.50,
                          "currency": "CHF",
                          "valueDate": "2026-03-30"
                        }
                        """)
            )
            .andExpect(status().isCreated())
            .andExpect(
                header().string(
                    "Location",
                    "/api/v1/transactions/44444444-4444-4444-4444-444444444444"
                )
            )
            .andExpect(jsonPath("$.referenceNo").value("TRX-0001"))
            .andExpect(jsonPath("$.type").value("DEBIT"))
            .andExpect(jsonPath("$.amount").value(125.5))
            .andExpect(jsonPath("$.currency").value("CHF"))
            .andExpect(jsonPath("$.status").value("RECEIVED"));
    }

    @Test
    void shouldReturnBadRequestWhenTransactionRequestIsInvalid() throws Exception {
        mockMvc.perform(
                post("/api/v1/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "referenceNo": "",
                          "accountId": null,
                          "type": null,
                          "amount": 0,
                          "currency": null,
                          "valueDate": null
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void shouldListTransactions() throws Exception {
        InternalTransaction transaction = InternalTransaction.rehydrate(
            UUID.fromString("55555555-5555-5555-5555-555555555555"),
            "TRX-0002",
            UUID.fromString("aac42efa-41fc-4e6d-8a5f-36040ff924f9"),
            TransactionType.CREDIT,
            new Money(new java.math.BigDecimal("200.0000"), CurrencyCode.CHF),
            LocalDate.parse("2026-03-30"),
            OffsetDateTime.parse("2026-03-30T20:00:00+02:00"),
            TransactionStatus.BOOKED
        );
        given(listInternalTransactionsUseCase.handle(any())).willReturn(List.of(transaction));

        mockMvc.perform(get("/api/v1/transactions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].referenceNo").value("TRX-0002"))
            .andExpect(jsonPath("$[0].status").value("BOOKED"));
    }
}
