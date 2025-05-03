package com.tech.engg5.events.router.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class KafkaConsumerPropertiesTest {

  @Test
  @DisplayName("Verify KafkaConsumerProperties with valid values")
  void givenValidKafkaConsumerPropertiesExpectsValidResponse() {
    KafkaConsumerProperties properties = new KafkaConsumerProperties();

    properties.setSeekType("beginning");
    properties.setRewindMessages(0);
    properties.setOffset(1);
    properties.setPartitionNumber(2);
    properties.setPauseConsuming(true);

    assertEquals("beginning", properties.getSeekType());
    assertEquals(0, properties.getRewindMessages());
    assertEquals(1, properties.getOffset());
    assertEquals(2, properties.getPartitionNumber());
    assertEquals(true, properties.pauseConsuming());
  }
}
