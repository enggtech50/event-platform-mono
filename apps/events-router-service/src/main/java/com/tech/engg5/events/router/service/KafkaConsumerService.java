package com.tech.engg5.events.router.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.engg5.common.exception.DatabaseException;
import com.tech.engg5.common.model.BookEventPayload;
import com.tech.engg5.consumer.properties.AbstractKafkaConsumerProperties;
import com.tech.engg5.consumer.service.AbstractKafkaConsumerService;
import com.tech.engg5.events.router.properties.KafkaConsumerProperties;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RefreshScope
public class KafkaConsumerService extends AbstractKafkaConsumerService {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumerService.class);

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BookRawEventService bookRawEventService;

  @Autowired
  protected KafkaConsumerProperties kafkaConsumerProperties;

  @Override
  protected AbstractKafkaConsumerProperties getKafkaConsumerProperties() {
    return kafkaConsumerProperties;
  }

  @KafkaListener(id = "KafkaConsumer", topics = "${events.router.consumer.topic}",
    groupId = "${events.router.consumer.group-id}", autoStartup = "${events.router.consumer.auto-startup:true}")
  public void consumeMessage(@Payload String message, @Header(value = KafkaHeaders.RECEIVED_KEY, required = false)
    String key, Acknowledgment ack, ConsumerRecordMetadata metadata) {
    super.consumeMessage(message, key, ack, metadata);
  }

  @Override
  protected void persistMessage(String message, ConsumerRecordMetadata metadata, String key) {
    LOG.info("Persisting message: [{}] from offset: [{}]", message, metadata.offset());

    try {
      BookEventPayload rawPayload = objectMapper.readValue(message, BookEventPayload.class);
      bookRawEventService.saveOrUpdateRawEvent(rawPayload, metadata.offset(), metadata.partition());
    } catch (Exception exc) {
      LOG.error("Error while persisting message: [{}] from offset: [{}]", message, metadata.offset(), exc);
      throw new DatabaseException("Error while persisting message", exc);
    }
  }

  @Override
  public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
    super.onPartitionsAssigned(assignments, callback);
  }
}
