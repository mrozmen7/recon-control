package com.yavuzozmen.reconcontrol.account.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yavuzozmen.reconcontrol.account.application.AccountNotFoundException;
import com.yavuzozmen.reconcontrol.account.application.GetAccountUseCase;
import com.yavuzozmen.reconcontrol.account.application.OpenAccountUseCase;
import com.yavuzozmen.reconcontrol.account.domain.Account;
import com.yavuzozmen.reconcontrol.account.domain.AccountStatus;
import com.yavuzozmen.reconcontrol.common.adapter.in.web.GlobalApiExceptionHandler;
import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;
import com.yavuzozmen.reconcontrol.common.domain.Money;
import com.yavuzozmen.reconcontrol.infra.SecurityConfig;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AccountController.class)
@Import({GlobalApiExceptionHandler.class, SecurityConfig.class})
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpenAccountUseCase openAccountUseCase;

    @MockBean
    private GetAccountUseCase getAccountUseCase;

    @Test
    void shouldOpenAccount() throws Exception {
        Account account = Account.rehydrate(
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            "CH1000000001",
            "cust-001",
            CurrencyCode.CHF,
            Money.zero(CurrencyCode.CHF),
            AccountStatus.ACTIVE
        );
        given(openAccountUseCase.handle(any())).willReturn(account);

        mockMvc.perform(
                post("/api/v1/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "accountNumber": "CH1000000001",
                          "customerId": "cust-001",
                          "currency": "CHF"
                        }
                        """)
            )
            .andExpect(status().isCreated())
            .andExpect(
                header().string(
                    "Location",
                    "/api/v1/accounts/11111111-1111-1111-1111-111111111111"
                )
            )
            .andExpect(jsonPath("$.accountNumber").value("CH1000000001"))
            .andExpect(jsonPath("$.currency").value("CHF"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldReturnAccountById() throws Exception {
        UUID accountId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        Account account = Account.rehydrate(
            accountId,
            "CH2000000002",
            "cust-002",
            CurrencyCode.EUR,
            Money.zero(CurrencyCode.EUR),
            AccountStatus.ACTIVE
        );
        given(getAccountUseCase.handle(eq(accountId))).willReturn(account);

        mockMvc.perform(get("/api/v1/accounts/{accountId}", accountId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(accountId.toString()))
            .andExpect(jsonPath("$.customerId").value("cust-002"))
            .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {
        UUID missingId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        given(getAccountUseCase.handle(eq(missingId))).willThrow(new AccountNotFoundException(missingId));

        mockMvc.perform(get("/api/v1/accounts/{accountId}", missingId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        mockMvc.perform(
                post("/api/v1/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "accountNumber": "",
                          "customerId": " ",
                          "currency": null
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}
