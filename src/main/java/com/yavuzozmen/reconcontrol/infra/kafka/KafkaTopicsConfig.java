package com.yavuzozmen.reconcontrol.infra.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("!test")
@EnableConfigurationProperties({KafkaTopicsProperties.class, KafkaConsumerProperties.class})
public class KafkaTopicsConfig {

    @Bean
    NewTopic transactionEventsTopic(KafkaTopicsProperties properties) {
        return TopicBuilder.name(properties.transactionEvents()).partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic transactionEventsDltTopic(KafkaTopicsProperties properties) {
        return TopicBuilder.name(properties.transactionEventsDlt()).partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic fraudAlertEventsTopic(KafkaTopicsProperties properties) {
        return TopicBuilder.name(properties.fraudAlertEvents()).partitions(1).replicas(1).build();
    }
}
