package com.tech.engg5.persistence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Abstract base repository interface for MongoDB entities.
 * <p>
 *   This interface is marked with {@link NoRepositoryBean}, indicating that Spring Data should not instantiate
 *   it directly as Spring Bean. Instead, it is meant to be extended by other concrete repository interfaces to
 *   share common functionality.
 * </p>
 * Extend this interface in the application-specific repositories to inherit standard MonogoDB CRUD operations and
 * centralize cross-cutting concerns.
 *
 * @param <T>  the domain or entity type the repository manages
 * @param <ID> the type of the entity's the repository manages
 */

@NoRepositoryBean
public interface AbstractMongoPersistenceRepository<T, ID> extends MongoRepository<T, ID> {
}
