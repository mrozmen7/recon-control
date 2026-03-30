package com.yavuzozmen.reconcontrol.transaction.application;

import com.yavuzozmen.reconcontrol.account.application.port.out.AccountRepository;
import com.yavuzozmen.reconcontrol.transaction.application.port.out.InternalTransactionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class TransactionApplicationConfig {

    @Bean
    CreateInternalTransactionUseCase createInternalTransactionUseCase(
        InternalTransactionRepository transactionRepository,
        AccountRepository accountRepository
    ) {
        return new CreateInternalTransactionUseCase(transactionRepository, accountRepository);
    }

    @Bean
    ListInternalTransactionsUseCase listInternalTransactionsUseCase(
        InternalTransactionRepository transactionRepository
    ) {
        return new ListInternalTransactionsUseCase(transactionRepository);
    }
}
