package com.yavuzozmen.reconcontrol.fraud.application;

import com.yavuzozmen.reconcontrol.fraud.application.port.out.FraudAlertEventPublisher;
import com.yavuzozmen.reconcontrol.fraud.application.port.out.FraudCaseRepository;
import com.yavuzozmen.reconcontrol.transaction.application.port.out.InternalTransactionRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
@EnableConfigurationProperties(FraudRulesProperties.class)
public class FraudApplicationConfig {

    @Bean
    FraudRuleEngine fraudRuleEngine(FraudRulesProperties properties) {
        return new FraudRuleEngine(properties);
    }

    @Bean
    EvaluateTransactionForFraudUseCase evaluateTransactionForFraudUseCase(
        FraudCaseRepository fraudCaseRepository,
        FraudAlertEventPublisher fraudAlertEventPublisher,
        InternalTransactionRepository internalTransactionRepository,
        FraudRuleEngine fraudRuleEngine,
        FraudRulesProperties properties
    ) {
        return new EvaluateTransactionForFraudUseCase(
            fraudCaseRepository,
            fraudAlertEventPublisher,
            internalTransactionRepository,
            fraudRuleEngine,
            properties
        );
    }

    @Bean
    ListFraudCasesUseCase listFraudCasesUseCase(FraudCaseRepository fraudCaseRepository) {
        return new ListFraudCasesUseCase(fraudCaseRepository);
    }

    @Bean
    GetFraudCaseUseCase getFraudCaseUseCase(FraudCaseRepository fraudCaseRepository) {
        return new GetFraudCaseUseCase(fraudCaseRepository);
    }
}
