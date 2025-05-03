package com.tech.engg5.events.router.properties;

import com.tech.engg5.consumer.properties.AbstractKafkaConsumerProperties;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Setter
@Component
@RefreshScope
public class KafkaConsumerProperties implements AbstractKafkaConsumerProperties {

  @Value("${events.router.consumer.seek-type:rewind}")
  private String seekType;

  @Value("${events.router.consumer.rewind-messages:0}")
  private int rewindMessages;

  @Value("${events.router.consumer.offset}")
  private long offset;

  @Value("${events.router.consumer.partition-number:-1}")
  private int partitionNumber;

  @Value("${events.router.consumer.pause-consuming:false}")
  private boolean pauseConsuming;

  @Override
  public String getSeekType() { return seekType; }

  @Override
  public int getRewindMessages() { return rewindMessages; }

  @Override
  public long getOffset() { return offset; }

  @Override
  public int getPartitionNumber() { return partitionNumber; }

  @Override
  public boolean pauseConsuming() { return pauseConsuming; }
}
