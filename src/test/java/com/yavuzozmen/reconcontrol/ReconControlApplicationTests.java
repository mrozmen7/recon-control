package com.yavuzozmen.reconcontrol;

import com.yavuzozmen.reconcontrol.account.application.GetAccountUseCase;
import com.yavuzozmen.reconcontrol.account.application.OpenAccountUseCase;
import com.yavuzozmen.reconcontrol.transaction.application.CreateInternalTransactionUseCase;
import com.yavuzozmen.reconcontrol.transaction.application.ListInternalTransactionsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ReconControlApplicationTests {

    @MockBean
    private OpenAccountUseCase openAccountUseCase;

    @MockBean
    private GetAccountUseCase getAccountUseCase;

    @MockBean
    private CreateInternalTransactionUseCase createInternalTransactionUseCase;

    @MockBean
    private ListInternalTransactionsUseCase listInternalTransactionsUseCase;

    @Test
    void contextLoads() {
    }
}
