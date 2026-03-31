package com.yavuzozmen.reconcontrol.infra.outbox;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
@EnableConfigurationProperties(OutboxPublisherProperties.class)
public class OutboxConfig {}
