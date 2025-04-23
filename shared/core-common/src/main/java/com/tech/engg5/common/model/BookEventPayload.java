package com.tech.engg5.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookEventPayload {

  @JsonProperty("alertDataId")
  String alertDataId;

  @JsonProperty("bookInfo")
  BookInfo bookInfo;

  @JsonProperty("transactionType")
  String transactionType;

  @JsonProperty("bookEventInfo")
  BookEventInfo bookEventInfo;
}
