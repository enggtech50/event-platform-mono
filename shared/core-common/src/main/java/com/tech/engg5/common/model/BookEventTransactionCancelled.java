package com.tech.engg5.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
