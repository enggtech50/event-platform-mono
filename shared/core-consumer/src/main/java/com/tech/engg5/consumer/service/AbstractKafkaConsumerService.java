package com.tech.engg5.consumer.service;

import com.tech.engg5.common.exception.MessageConsumptionException;
import com.tech.engg5.consumer.constants.ConsumerConstants;
import com.tech.engg5.consumer.properties.AbstractKafkaConsumerProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RefreshScope
public abstract class AbstractKafkaConsumerService implements ConsumerSeekAware {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractKafkaConsumerService.class);

  private int PAUSE_DURATION_MS = 60000;

  @Autowired
  protected KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  protected abstract AbstractKafkaConsumerProperties getKafkaConsumerProperties();

  protected abstract void persistMessage(String message, ConsumerRecordMetadata metadata, String key);

  protected void consumeMessage(String message, String key, Acknowledgment ack, ConsumerRecordMetadata metadata) {
    LOG.info("Received message: [{}], from offset - [{}]", message, metadata.offset());
    persistMessage(message, metadata, key);
    ack.acknowledge();
    LOG.info("Acknowledged message: [{}], from offset - [{}], partitionNumber - [{}]", message, metadata.offset(),
      metadata.partition());

    if (pauseConsuming()) {
      LOG.info("Stop consuming messages.");
      pauseMessageConsumption();
    }
  }

  public void assignPartition(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
    String seekType = getSeekType();
    long offset = getOffset();
    int rewindMessages = getRewindMessages();
    int partitionNumber = getPartitionNumber();

    if (StringUtils.isNotBlank(seekType)) {
      if (offset > 0) {
        assignments.keySet().forEach(tp -> {
          if (partitionNumber == -1 || partitionNumber == tp.partition()) {
            LOG.info("Seeking offset for Topic - " + tp.topic() + " Partition - " + tp.partition() + " Offset - " + offset);
            callback.seek(tp.topic(), tp.partition(), offset);
          }
        });
      } else if (ConsumerConstants.REWIND.equalsIgnoreCase(seekType)) {
        if (rewindMessages < 0) {
          LOG.info("Rewinding [{}] messages in each partition.", rewindMessages);
          assignments.keySet()
            .forEach(tp -> callback.seekRelative(tp.topic(), tp.partition(), rewindMessages, true));
        }
      } else if (ConsumerConstants.REWIND_FROM_END.equalsIgnoreCase(seekType)) {
        if (rewindMessages < 0) {
          LOG.info("Rewinding [{}] messages in each partition from end.", rewindMessages);
          assignments.keySet()
            .forEach(tp -> callback.seekRelative(tp.topic(), tp.partition(), rewindMessages, false));
        }
      } else if (ConsumerConstants.SEEK_TO_BEGINNING.equalsIgnoreCase(seekType)) {
        LOG.info("Seeking right back to the beginning in each partition.");
        assignments.keySet().forEach(tp -> callback.seekToBeginning(tp.topic(), tp.partition()));
      } else if (ConsumerConstants.SEEK_TO_END.equalsIgnoreCase(seekType)) {
        LOG.info("Seeking to the end in each partition.");
        assignments.keySet().forEach(tp -> callback.seekToEnd(tp.topic(), tp.partition()));
      } else {
        throw new MessageConsumptionException("Seek_type was set to " + seekType + " but can only be one of "
          + ConsumerConstants.REWIND + "," + ConsumerConstants.SEEK_TO_BEGINNING + "," + ConsumerConstants.SEEK_TO_END);
      }
    }
  }

  protected void pauseMessageConsumption() {
    kafkaListenerEndpointRegistry.getListenerContainer(getListnerContainerId()).stop();

    while (true) {
      try {
        Thread.sleep(PAUSE_DURATION_MS);
      } catch (InterruptedException exc) {
        LOG.error("Thread interrupted in method pauseMessageConsumption(), restart pod");
        throw new MessageConsumptionException(exc);
      }
      if (!pauseConsuming()) {
        LOG.info("Resuming consuming messages immediately");
        kafkaListenerEndpointRegistry.getListenerContainer(getListnerContainerId()).start();
        break;
      }
    }
  }

  protected String getSeekType() {
    return getKafkaConsumerProperties().getSeekType();
  }

  protected long getOffset() {
    return getKafkaConsumerProperties().getOffset();
  }

  protected int getRewindMessages() {
    return getKafkaConsumerProperties().getRewindMessages();
  }

  protected int getPartitionNumber() {
    return getKafkaConsumerProperties().getPartitionNumber();
  }

  protected boolean pauseConsuming() {
    return getKafkaConsumerProperties().pauseConsuming();
  }

  public String getListnerContainerId() {
    return "KafkaConsumer";
  }

}
