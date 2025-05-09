package com.tech.engg5.events.router;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.IOUtils.toByteArray;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Fixture {
  RAW_REQUESTS("raw");

  String path;

  @SneakyThrows
  public String loadFixture(String filename) {
    String fixturePath = "fixtures/" + this.path + '/' + filename;
    try (InputStream inputStream = new ClassPathResource(fixturePath).getInputStream()) {
      return new String(toByteArray(inputStream), UTF_8);
    }
  }
}
