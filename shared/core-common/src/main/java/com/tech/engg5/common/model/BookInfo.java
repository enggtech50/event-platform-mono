package com.tech.engg5.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookInfo {

  @JsonProperty("bookId")
  String bookId;

  @JsonProperty("bookName")
  String bookName;

  @JsonProperty("bookGenre")
  List<String> bookGenre;

  @JsonProperty("bookPublisher")
  List<String> bookPublisher;

  @JsonProperty("bookPublishingDate")
  String bookPublishingDate;

  @JsonProperty("bookAuthor")
  String bookAuthor;
}
