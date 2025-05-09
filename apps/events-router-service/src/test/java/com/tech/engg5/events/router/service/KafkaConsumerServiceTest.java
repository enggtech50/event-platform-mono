package com.tech.engg5.events.router.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.engg5.common.model.BookEventPayload;
import com.tech.engg5.events.router.Fixture;
import com.tech.engg5.events.router.properties.KafkaConsumerProperties;
import com.tech.engg5.events.router.repository.BookEventRawMongoRepository;
import com.tech.engg5.persistence.model.mongo.BookEventRaw;
import lombok.SneakyThrows;
import lombok.val;
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

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private BookRawEventService bookRawEventService;

  @BeforeEach
  void setUp() {
    kafkaConsumerService = new KafkaConsumerService();
    ReflectionTestUtils.setField(kafkaConsumerService, "kafkaConsumerProperties", consumerProperties);
    ReflectionTestUtils.setField(kafkaConsumerService, "objectMapper", objectMapper);
    ReflectionTestUtils.setField(kafkaConsumerService, "bookRawEventService", bookRawEventService);
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
  @SneakyThrows
  @DisplayName("Test persistMessage logs message and offset")
  void testPersistMessage(CapturedOutput output) {
    String message = Fixture.RAW_REQUESTS.loadFixture("raw-message.json");
    String key = "test-key";
    when(metadata.offset()).thenReturn(123L);
    when(metadata.partition()).thenReturn(0);
    BookEventPayload payload = new BookEventPayload();
    when(objectMapper.readValue(message, BookEventPayload.class)).thenReturn(payload);

    kafkaConsumerService.persistMessage(message, metadata, key);

    assertThat(output).containsSequence("Persisting message: [" + message + "] from offset: [123]");
    verify(bookRawEventService, times(1)).saveOrUpdateRawEvent(payload, 123L, 0);
  }

  @Test
  @DisplayName("Test onPartitionsAssigned calls super method")
  void testOnPartitionsAssigned() {
    Map<TopicPartition, Long> assignments = new HashMap<>();
    assignments.put(new TopicPartition("test-topic", 0), 123L);

    kafkaConsumerService.onPartitionsAssigned(assignments, callback);

    verify(callback, never()).seek(anyString(), anyInt(), anyLong());
  }

  @Test
  @SneakyThrows
  @DisplayName("Verify raw message is deserialized to object")
  void shouldConvertRawMessagetoObject() {

    String message = Fixture.RAW_REQUESTS.loadFixture("raw-message.json");
    BookEventPayload payload = BookEventPayload.builder().alertDataId("tua12da-12132nd1-12poij").build();

    when(objectMapper.readValue(message, BookEventPayload.class)).thenReturn(payload);

    val event = objectMapper.readValue(message, BookEventPayload.class);

    assertThat(event).isNotNull();
    assertThat(event.getAlertDataId()).isEqualTo("tua12da-12132nd1-12poij");
  }
}
