package com.tech.engg5.events.router.service;

import com.tech.engg5.common.model.BookEventPayload;
import com.tech.engg5.events.router.repository.BookEventRawMongoRepository;
import com.tech.engg5.persistence.model.mongo.Audit;
import com.tech.engg5.persistence.model.mongo.BookEventRaw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class BookRawEventService {

  private static final Logger LOG = LoggerFactory.getLogger(BookRawEventService.class);

  @Autowired
  BookEventRawMongoRepository bookEventRawMongoRepository;

  public void saveOrUpdateRawEvent(BookEventPayload payload, long offset, int partitionNumber) {

    Optional<BookEventRaw> existingRecord = bookEventRawMongoRepository.findById(payload.getAlertDataId());

    if (existingRecord.isPresent()) {
      LOG.info("Record with alertId - [{}] already exists within database.", payload.getAlertDataId());
    } else {
      LOG.info("Record with alertId - [{}] does not exist within database. Saving the record.", payload.getAlertDataId());

      BookEventRaw record = BookEventRaw.builder()
        .alertId(payload.getAlertDataId())
        .bookEvent(payload)
        .audit(Audit.builder()
          .creationDate(Instant.now())
          .lastModifiedDate(Instant.now())
          .lastModifiedBy("EVENT_ROUTER_SERVICE")
          .messageOffset(offset)
          .messagePartition(partitionNumber)
          .build())
        .build();

      bookEventRawMongoRepository.save(record);
    }
  }
}
