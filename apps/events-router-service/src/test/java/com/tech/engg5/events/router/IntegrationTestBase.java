package com.tech.engg5.events.router;

import com.tech.engg5.persistence.model.mongo.Audit;
import org.assertj.core.api.RecursiveComparisonAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class IntegrationTestBase {

  @Autowired
  protected MongoTemplate mongoTemplate;

  protected static final List<String> DEFAULT_FIELDS_TO_IGNORE_RAW_AUDIT =
    asList(Audit.Fields.creationDate, Audit.Fields.lastModifiedDate);

  protected static final Duration EXPECTING_MESSAGES_TIMEOUT = Duration.ofSeconds(500);

  @SafeVarargs
  protected final <T> void thenExpectEntriesInRaw(Class<T> type, T... expectedEntries) {
    thenExpectDatabaseEntries(type, DEFAULT_FIELDS_TO_IGNORE_RAW_AUDIT, expectedEntries);
  }

  @SafeVarargs
  protected final <T> void thenExpectDatabaseEntries(Class<T> type, List<String> fieldsToIgnore, T... expectedEntries) {
    await().atMost(EXPECTING_MESSAGES_TIMEOUT)
      .until(() -> mongoTemplate.findAll(type).size() == expectedEntries.length);

    List<T> entriesInCollection = mongoTemplate.findAll(type);
    RecursiveComparisonAssert<?> assertion = assertThat(entriesInCollection).usingRecursiveComparison()
      .ignoringAllOverriddenEquals()
      .ignoringFields(fieldsToIgnore.toArray(new String[0]))
      .ignoringCollectionOrder();

    assertion.isEqualTo(Arrays.asList(expectedEntries));
  }
}
