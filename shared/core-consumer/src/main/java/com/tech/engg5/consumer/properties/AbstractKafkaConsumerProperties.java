package com.tech.engg5.consumer.properties;

public interface AbstractKafkaConsumerProperties {

  //Values related to kafka consumer topic
  public String getSeekType();
  public int getRewindMessages();
  public long getOffset();
  public int getPartitionNumber();
  public boolean pauseConsuming();
}
