package com.tech.engg5.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
