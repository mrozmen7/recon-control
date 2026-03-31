package com.yavuzozmen.reconcontrol.transaction.application;

import com.yavuzozmen.reconcontrol.account.application.port.out.AccountRepository;
import com.yavuzozmen.reconcontrol.infra.idempotency.TransactionIdempotencyProperties;
import com.yavuzozmen.reconcontrol.transaction.application.port.out.InternalTransactionRepository;
import com.yavuzozmen.reconcontrol.transaction.application.port.out.TransactionEventPublisher;
import com.yavuzozmen.reconcontrol.transaction.application.port.out.TransactionIdempotencyStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
@EnableConfigurationProperties(TransactionIdempotencyProperties.class)
public class TransactionApplicationConfig {

    @Bean
    CreateInternalTransactionUseCase createInternalTransactionUseCase(
        InternalTransactionRepository transactionRepository,
        AccountRepository accountRepository,
        TransactionIdempotencyStore idempotencyStore,
        TransactionEventPublisher transactionEventPublisher,
        TransactionIdempotencyProperties properties
    ) {
        return new CreateInternalTransactionUseCase(
            transactionRepository,
            accountRepository,
            idempotencyStore,
            transactionEventPublisher,
            properties.ttl()
        );
    }

    @Bean
    ListInternalTransactionsUseCase listInternalTransactionsUseCase(
        InternalTransactionRepository transactionRepository
    ) {
        return new ListInternalTransactionsUseCase(transactionRepository);
    }

    @Bean
    GetInternalTransactionUseCase getInternalTransactionUseCase(
        InternalTransactionRepository transactionRepository
    ) {
        return new GetInternalTransactionUseCase(transactionRepository);
    }

    @Bean
    MarkTransactionSettlementPendingUseCase markTransactionSettlementPendingUseCase(
        InternalTransactionRepository transactionRepository,
        TransactionEventPublisher transactionEventPublisher
    ) {
        return new MarkTransactionSettlementPendingUseCase(
            transactionRepository,
            transactionEventPublisher
        );
    }

    @Bean
    SettleTransactionUseCase settleTransactionUseCase(
        InternalTransactionRepository transactionRepository,
        TransactionEventPublisher transactionEventPublisher
    ) {
        return new SettleTransactionUseCase(transactionRepository, transactionEventPublisher);
    }
}
