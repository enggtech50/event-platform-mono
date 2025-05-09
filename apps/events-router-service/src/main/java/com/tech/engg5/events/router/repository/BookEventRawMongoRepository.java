package com.tech.engg5.events.router.repository;

import com.tech.engg5.persistence.model.mongo.BookEventRaw;
import com.tech.engg5.persistence.repository.AbstractMongoPersistenceRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookEventRawMongoRepository extends AbstractMongoPersistenceRepository<BookEventRaw, String> {
}
