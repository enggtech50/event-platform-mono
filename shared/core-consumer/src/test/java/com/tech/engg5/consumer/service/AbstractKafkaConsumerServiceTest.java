package com.tech.engg5.consumer.service;

import com.tech.engg5.common.exception.MessageConsumptionException;
import com.tech.engg5.consumer.constants.ConsumerConstants;
import com.tech.engg5.consumer.properties.AbstractKafkaConsumerProperties;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AbstractKafkaConsumerServiceTest {

  @Mock
  Acknowledgment ack;
  private Map<TopicPartition, Long> assignmentsHavingOneTopic;
  private Map<TopicPartition, Long> assignmentsHavingThreeTopics;

  @Mock
  private ConsumerSeekAware.ConsumerSeekCallback callback;

  @Mock
  private KafkaListenerEndpointRegistry registry;

  @Mock
  private MessageListenerContainer container;

  @BeforeEach
  void setUp() {
    assignmentsHavingOneTopic = new HashMap<TopicPartition, Long>();
    assignmentsHavingOneTopic.put(new TopicPartition("test", 1), Long.valueOf(1));
    assignmentsHavingThreeTopics = new HashMap<TopicPartition, Long>();
    assignmentsHavingThreeTopics.put(new TopicPartition("test", 1), Long.valueOf(1));
    assignmentsHavingThreeTopics.put(new TopicPartition("test2", 2), Long.valueOf(1));
    assignmentsHavingThreeTopics.put(new TopicPartition("test3", 3), Long.valueOf(1));
  }

  @Test
  @DisplayName("Verify consumeMessage pauses consumption and persists message correctly")
  void givenConsumerWithPauseExpectMethodCallsCorrectly() {
    PropertiesProvider provider = new PropertiesProvider(true);

    TestConsumer t = new TestConsumer(provider);
    TopicPartition partition = new TopicPartition("topic", 0);
    RecordMetadata recordMeta = new RecordMetadata(partition, 0, 0, 0, 0, 0);
    ConsumerRecordMetadata rec = new ConsumerRecordMetadata(recordMeta, null);
    ReflectionTestUtils.setField(t, "kafkaListenerEndpointRegistry", registry);
    ReflectionTestUtils.setField(t, "PAUSE_DURATION_MS", 1);

    t.consumeMessage("bob", "hello", ack, rec);
    provider.setOffset(3);
    provider.setRewindCount(1);
    provider.setPartition(4);
    provider.setSeekType(ConsumerConstants.REWIND);
    assertTrue(t.persistViaStringCalled);
    assertTrue(t.pauseCalled);

    assertEquals(ConsumerConstants.REWIND, t.getSeekType());
    assertEquals(3, t.getOffset());
    assertEquals(1, t.getRewindMessages());
    assertEquals(4, t.getPartitionNumber());
    assertEquals("KafkaConsumer", t.getListnerContainerId());
  }

  @Test
  @DisplayName("Verify consumeMessage persists message and does not pause consumption when pauseConsuming is false")
  void givenConsumerExpectMethodCallsCorrectly() {
    PropertiesProvider provider = new PropertiesProvider(false);
    TestConsumer t = new TestConsumer(provider);
    TopicPartition partition = new TopicPartition("topic", 0);
    RecordMetadata recordMeta = new RecordMetadata(partition, 0, 0, 0, 0, 0);

    ConsumerRecordMetadata rec = new ConsumerRecordMetadata(recordMeta, null);

    t.consumeMessage("hello world", "greetings", ack, rec);

    assertTrue(t.persistViaStringCalled);
    assertFalse(t.pauseCalled);

  }

  @Test
  @DisplayName("Verify assignPartition does not call seekRelative when rewindMessages is not set")
  void givenNoRewindMessagesValueDoNotSeekRelativeInParition() {
    PropertiesProvider provider = new PropertiesProvider(true);

    TestConsumer t = new TestConsumer(provider);
    t.assignPartition(assignmentsHavingOneTopic, callback);
    verify(callback, times(0)).seekRelative(anyString(), anyInt(), anyLong(), anyBoolean());
  }

  @Test
  @DisplayName("Verify assignPartition does not rewind when rewindMessages is positive for REWIND")
  void givenPositiveRewindMessagesDoNotSeekRelativeInPartition() {
    PropertiesProvider provider = new PropertiesProvider(true);
    TestConsumer consumer = new TestConsumer(provider);
    provider.setRewindCount(100);
    provider.setSeekType(ConsumerConstants.REWIND);
    consumer.assignPartition(assignmentsHavingOneTopic, callback);
    verify(callback, times(0)).seekRelative(anyString(), anyInt(), anyLong(), anyBoolean());
  }

  @Test
  @DisplayName("Verify assignPartition rewinds correctly for negative rewindMessages with REWIND")
  void givenNegativeRewindMessagesValueSeekRelativeInParition() {
    PropertiesProvider provider = new PropertiesProvider(true);
    TestConsumer consumer = new TestConsumer(provider);
    provider.setRewindCount(-100);
    provider.setSeekType(ConsumerConstants.REWIND);
    consumer.assignPartition(assignmentsHavingOneTopic, callback);
    verify(callback, times(1)).seekRelative("test", 1, -100, true);
  }

  @Test
  @DisplayName("Verify assignPartition rewinds correctly for negative rewindMessages with REWIND_FROM_END")
  void givenNegativeRewindFromEndMessagesValueSeekRelativeInPartition() {
    PropertiesProvider provider = new PropertiesProvider(true);
    TestConsumer consumer = new TestConsumer(provider);
    provider.setRewindCount(-100);
    provider.setSeekType(ConsumerConstants.REWIND_FROM_END);
    consumer.assignPartition(assignmentsHavingOneTopic, callback);
    verify(callback, times(1)).seekRelative("test", 1, -100, false);
  }

  @Test
  @DisplayName("Verify assignPartition does not rewind when rewindMessages is positive for REWIND_FROM_END")
  void givenPositiveRewindFromEndMessagesValueSeekRelativeInPartition() {
    PropertiesProvider provider = new PropertiesProvider(true);
    TestConsumer consumer = new TestConsumer(provider);
    provider.setRewindCount(100);
    provider.setSeekType(ConsumerConstants.REWIND_FROM_END);
    consumer.assignPartition(assignmentsHavingOneTopic, callback);
    verify(callback, times(0)).seekRelative(anyString(), anyInt(), anyLong(), anyBoolean());
  }

  @Test
  @DisplayName("Verify assignPartition calls seekToBeginning for the specified topic and partition")
  void givenSeekToBeginningCallCorrectMethod() {
    PropertiesProvider provider = new PropertiesProvider(true);
    TestConsumer consumer = new TestConsumer(provider);
    provider.setSeekType(ConsumerConstants.SEEK_TO_BEGINNING);
    consumer.assignPartition(assignmentsHavingOneTopic, callback);
    verify(callback, times(1)).seekToBeginning("test", 1);
  }

  @Test
  @DisplayName("Verify assignPartition calls seekToEnd for the specified topic and partition")
  void givenSeekToEndCallCorrectMethod() {
    PropertiesProvider provider = new PropertiesProvider(true);
    TestConsumer consumer = new TestConsumer(provider);
    provider.setSeekType(ConsumerConstants.SEEK_TO_END);
    consumer.assignPartition(assignmentsHavingOneTopic, callback);
    verify(callback, times(1)).seekToEnd("test", 1);
  }

  @Test
  @DisplayName("Verify assignPartition throws KafkaException for invalid seekType")
  void givenInvalidSeekTypeThrowKafkaException() {
    PropertiesProvider provider = new PropertiesProvider(true);
    TestConsumer consumer = new TestConsumer(provider);
    provider.setSeekType("test");

    MessageConsumptionException thrown = assertThrows(MessageConsumptionException.class,
      () -> consumer.assignPartition(assignmentsHavingOneTopic, callback));
    assertEquals("Seek_type was set to test but can only be one of rewind,beginning,end", thrown.getMessage());
  }

  @Test
  @DisplayName("Verify assignPartition does nothing when seekType is blank or null")
  void givenABlankSeekTypeDoNothing() {
    PropertiesProvider provider = new PropertiesProvider(true);
    TestConsumer consumer = new TestConsumer(provider);
    provider.setSeekType("");
    consumer.onPartitionsAssigned(assignmentsHavingOneTopic, callback);
    verify(callback, times(0)).seekRelative(anyString(), anyInt(), anyLong(), anyBoolean());
    provider.setSeekType(null);
    consumer.assignPartition(assignmentsHavingOneTopic, callback);
    verify(callback, times(0)).seekRelative(anyString(), anyInt(), anyLong(), anyBoolean());
  }

  @Test
  @DisplayName("Verify assignPartition seeks to the specified offset for all partitions when partition is set to 2")
  void givenValidSeekTypeAndPositiveNumberOffsetAndPositiveNumberPartitionCorrectSeekInPartition() {
    PropertiesProvider provider = new PropertiesProvider(true);
    TestConsumer consumer = new TestConsumer(provider);
    provider.setSeekType(ConsumerConstants.SEEK_TO_BEGINNING);
    provider.setOffset(10000);
    provider.setPartition(2);
    consumer.assignPartition(assignmentsHavingThreeTopics, callback);
    verify(callback, times(1)).seek("test2", 2, 10000L);
    verify(callback, times(0)).seek("test", 1, 10000L);
    verify(callback, times(0)).seek("test3", 3, 10000L);
  }

  @Test
  @DisplayName("Verify assignPartition seeks to the specified offset for all partitions when partition is set to -1")
  void givenValidSeekTypeAndPositiveNumberOffsetAndMinusOneAsPartitionSeekInAllThreePartitions() {
    PropertiesProvider provider = new PropertiesProvider(true);
    TestConsumer consumer = new TestConsumer(provider);
    provider.setSeekType(ConsumerConstants.SEEK_TO_BEGINNING);
    provider.setOffset(10000);
    provider.setPartition(-1);
    consumer.assignPartition(assignmentsHavingThreeTopics, callback);
    verify(callback, times(1)).seek("test", 1, 10000L);
    verify(callback, times(1)).seek("test2", 2, 10000L);
    verify(callback, times(1)).seek("test3", 3, 10000L);

  }

  @Test
  @DisplayName("Verify that pauseMessageConsumption stops and resumes the Kafka listener container correctly")
  void givenPausedMessagesExpectCallToStopContainer() {
    MockitoAnnotations.openMocks(this);
    PropertiesProvider provider = new PropertiesProvider(false);

    TestConsumer consumer = new TestConsumer(provider);
    consumer.callSuper = true;
    ReflectionTestUtils.setField(consumer, "kafkaListenerEndpointRegistry", registry);
    ReflectionTestUtils.setField(consumer, "PAUSE_DURATION_MS", 10);
    Mockito.when(registry.getListenerContainer(any())).thenReturn(container);
    consumer.pauseMessageConsumption();

    verify(registry, times(2)).getListenerContainer(anyString());

  }

  class TestConsumer extends AbstractKafkaConsumerService {
    protected boolean persistViaStringCalled = false;
    protected boolean pauseCalled = false;
    protected boolean callSuper = false;

    AbstractKafkaConsumerProperties props;

    public TestConsumer(AbstractKafkaConsumerProperties props) { this.props = props; }

    @Override
    protected void persistMessage(String message, ConsumerRecordMetadata metadata, String key) {
      persistViaStringCalled = true;
    }

    @Override
    protected AbstractKafkaConsumerProperties getKafkaConsumerProperties() {
      return props;
    }

    @Override
    public void pauseMessageConsumption() {
      pauseCalled = true;
      if (callSuper) {
        super.pauseMessageConsumption();
      }
    }
  }

  class PropertiesProvider implements AbstractKafkaConsumerProperties {

    String seekType;
    int rewindCount = 0;
    int offset = 0;
    int partition = 0;
    boolean pauseMain = false;

    public void setSeekType(String seekType) { this.seekType = seekType; }

    public void setRewindCount(int rewindCount) { this.rewindCount = rewindCount; }

    public void setOffset(int offset) { this.offset = offset; }

    public void setPartition(int partition) { this.partition = partition; }

    public PropertiesProvider(boolean pauseMain) {
      this.pauseMain = pauseMain;
    }

    @Override
    public String getSeekType() { return seekType; }

    @Override
    public int getRewindMessages() { return rewindCount; }

    @Override
    public long getOffset() { return offset; }

    @Override
    public int getPartitionNumber() { return partition; }

    @Override
    public boolean pauseConsuming() { return pauseMain; }
  }

  @Test
  @DisplayName("Verify that pauseMessageConsumption handles InterruptedException correctly")
  void givenPausedMessagesAsTrueMakeJacocoHappy() throws InterruptedException {
    MockitoAnnotations.openMocks(this);
    PropertiesProvider provider = new PropertiesProvider(false);
    TestConsumer consumer = new TestConsumer(provider);
    consumer.callSuper = true;
    ReflectionTestUtils.setField(consumer, "kafkaListenerEndpointRegistry", registry);
    ReflectionTestUtils.setField(consumer, "PAUSE_DURATION_MS", 10);
    Mockito.when(registry.getListenerContainer(any())).thenReturn(container);
    Thread thread = new Thread(() -> {
      assertThrows(InterruptedException.class, consumer::pauseMessageConsumption);
    });
    thread.start();
    thread.interrupt();
    thread.join();
  }
}
