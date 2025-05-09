package com.tech.engg5.persistence.model.mongo;

import com.tech.engg5.common.model.BookEventPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Document(collection = "book-events-raw")
public class BookEventRaw {

  @Id
  String alertId;
  BookEventPayload bookEvent;
  Audit audit;
}
