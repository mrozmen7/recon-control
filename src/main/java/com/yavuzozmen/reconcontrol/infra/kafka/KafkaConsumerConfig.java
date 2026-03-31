package com.yavuzozmen.reconcontrol.infra.kafka;

import java.util.Objects;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Profile("!test")
public class KafkaConsumerConfig {

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
        ConsumerFactory<String, String> consumerFactory,
        KafkaTemplate<String, String> kafkaTemplate,
        KafkaTopicsProperties topicsProperties
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setObservationEnabled(false);
        factory.setCommonErrorHandler(defaultErrorHandler(kafkaTemplate, topicsProperties));
        return factory;
    }

    @Bean
    DefaultErrorHandler defaultErrorHandler(
        KafkaTemplate<String, String> kafkaTemplate,
        KafkaTopicsProperties topicsProperties
    ) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
            kafkaTemplate,
            (record, exception) ->
                new TopicPartition(topicsProperties.transactionEventsDlt(), record.partition())
        );
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
            recoverer,
            new FixedBackOff(250L, 2L)
        );
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        return errorHandler;
    }
}
