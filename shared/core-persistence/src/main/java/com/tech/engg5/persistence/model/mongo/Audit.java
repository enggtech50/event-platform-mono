package com.tech.engg5.persistence.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Audit {

  Instant creationDate;

  @LastModifiedDate
  Instant lastModifiedDate;

  String lastModifiedBy;
  long messageOffset;
  int messagePartition;
}
