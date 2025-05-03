package com.tech.engg5.events.router;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class EventsRouterApplication {

  public static void main(String[] args) {
    SpringApplication.run(EventsRouterApplication.class, args);
  }
}
