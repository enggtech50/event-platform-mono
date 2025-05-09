package com.tech.engg5.events.router.service;

import com.tech.engg5.common.model.BookEventPayload;
import com.tech.engg5.events.router.repository.BookEventRawMongoRepository;
import com.tech.engg5.persistence.model.mongo.Audit;
import com.tech.engg5.persistence.model.mongo.BookEventRaw;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookRawEventServiceTest {

  private BookRawEventService bookRawEventService;

  @Mock
  private BookEventRawMongoRepository bookEventRawMongoRepository;

  @BeforeEach
  @AfterEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    bookRawEventService = new BookRawEventService();
    bookRawEventService.bookEventRawMongoRepository = bookEventRawMongoRepository;
  }

  @Test
  @DisplayName("Verify the saveOrUpdateRawEvent method saves a new record when it does not exist in the database")
  void shouldSaveRawMessage_toDatabase() {

    String alertId = "test-alert-id";
    BookEventPayload payload = BookEventPayload.builder().alertDataId(alertId).build();

    when(bookEventRawMongoRepository.findById(alertId)).thenReturn(Optional.empty());
    bookRawEventService.saveOrUpdateRawEvent(payload, 123L, 1);

    ArgumentCaptor<BookEventRaw> argumentCaptor = ArgumentCaptor.forClass(BookEventRaw.class);
    verify(bookEventRawMongoRepository, times(1)).save(argumentCaptor.capture());
    BookEventRaw savedRecord = argumentCaptor.getValue();

    assertThat(savedRecord.getAlertId()).isEqualTo(alertId);
    assertThat(savedRecord.getAudit().getMessageOffset()).isEqualTo(123L);
    assertThat(savedRecord.getAudit().getMessagePartition()).isEqualTo(1);
  }

  @Test
  @DisplayName("Verify the saveOrUpdateRawEvent method does not save a record when it already exists in the database")
  void shouldReturnWhenRecordExists() {
    String alertId = "test-alert-id";
    BookEventPayload payload = BookEventPayload.builder().alertDataId(alertId).build();

    BookEventRaw existingRecord = BookEventRaw.builder().alertId(alertId).bookEvent(payload)
      .audit(Audit.builder().messageOffset(123L).messagePartition(1).build())
      .build();

    when(bookEventRawMongoRepository.findById(alertId)).thenReturn(Optional.of(existingRecord));

    bookRawEventService.saveOrUpdateRawEvent(payload, 123L, 1);

    verify(bookEventRawMongoRepository, never()).save(any(BookEventRaw.class));
    verify(bookEventRawMongoRepository, times(1)).findById(alertId);
  }
}
