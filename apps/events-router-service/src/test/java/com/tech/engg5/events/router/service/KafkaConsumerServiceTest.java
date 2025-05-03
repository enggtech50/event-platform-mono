package com.tech.engg5.events.router.service;

import com.tech.engg5.events.router.properties.KafkaConsumerProperties;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
public class KafkaConsumerServiceTest {

  private KafkaConsumerService kafkaConsumerService;

  @Mock
  private KafkaConsumerProperties consumerProperties;

  @Mock
  private Acknowledgment ack;

  @Mock
  private ConsumerRecordMetadata metadata;

  @Mock
  private ConsumerSeekAware.ConsumerSeekCallback callback;

  @BeforeEach
  void setUp() {
    kafkaConsumerService = new KafkaConsumerService();
    ReflectionTestUtils.setField(kafkaConsumerService, "kafkaConsumerProperties", consumerProperties);
  }

  @Test
  @DisplayName("Verify the consume method consumes the messages then acknowledges back")
  void testConsumeMethod() {
    String message = "test-message";
    String key = "test-key";

    kafkaConsumerService.consumeMessage(message, key, ack, metadata);

    verify(ack, times(1)).acknowledge();
  }

  @Test
  @DisplayName("Test persistMessage logs message and offset")
  void testPersistMessage(CapturedOutput output) {
    String message = "test-message";
    String key = "test-key";
    when(metadata.offset()).thenReturn(123L);

    kafkaConsumerService.persistMessage(message, metadata, key);

    assertThat(output).containsSequence("Persisting message: [test-message] from offset: [123]");
  }

  @Test
  @DisplayName("Test onPartitionsAssigned calls super method")
  void testOnPartitionsAssigned() {
    Map<TopicPartition, Long> assignments = new HashMap<>();
    assignments.put(new TopicPartition("test-topic", 0), 123L);

    kafkaConsumerService.onPartitionsAssigned(assignments, callback);

    verify(callback, never()).seek(anyString(), anyInt(), anyLong());
  }
}
