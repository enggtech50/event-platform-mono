package com.tech.engg5.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookEventTransactionCancelled {

  @JsonProperty("transactionId")
  String transactionId;

  @JsonProperty("transactionAmount")
  String transactionAmount;

  @JsonProperty("transactionMethodLastFourDigits")
  String transactionMethodLastFourDigits;

  @JsonProperty("transactionDate")
  String transactionDate;

  @JsonProperty("transactionPayeeName")
  String transactionPayeeName;
}
