package com.yavuzozmen.reconcontrol.account.application;

import com.yavuzozmen.reconcontrol.account.application.port.out.AccountRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Profile("!test")
public class AccountApplicationConfig {

    @Bean
    OpenAccountUseCase openAccountUseCase(AccountRepository accountRepository) {
        return new OpenAccountUseCase(accountRepository);
    }

    @Bean
    GetAccountUseCase getAccountUseCase(AccountRepository accountRepository) {
        return new GetAccountUseCase(accountRepository);
    }
}
