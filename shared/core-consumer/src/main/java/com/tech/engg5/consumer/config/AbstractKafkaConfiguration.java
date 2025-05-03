package com.tech.engg5.consumer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
public abstract class AbstractKafkaConfiguration {

  @Bean
  public DefaultErrorHandler defaultErrorHandler() {
    return new DefaultErrorHandler(new FixedBackOff(1000L, 3L));
  }
}
