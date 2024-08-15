/*
 * CrudRepository.java        1.0 2024/08/14
 */
package com.interfaces;

import java.util.List;

/**
 * A generic repository interface that provides basic CRUD (Create, Read, Update) operations for a
 * given entity type {@code T}.
 *
 * @author Александр Зайбель
 * @version 1.0
 */
public interface CrudRepository<T> {

  /**
   * Creates a new entity in the underlying data store.
   *
   * @param entity the entity to be created
   */
  void create(T entity);

  /**
   * Creates a batch of entities in the underlying data store.
   *
   * @param entities the list of entities to be created
   */
  void createBatch(List<T> entities);

  /**
   * Retrieves all the entities managed by this repository.
   *
   * @return a list of all the entities
   */
  List<T> get();

  /**
   * Finds an entity by its unique identifier.
   *
   * @param id the unique identifier of the entity
   * @return the entity with the specified ID, or null if not found
   */
  T findById(int id);

  /**
   * Updates an existing entity in the underlying data store.
   *
   * @param entity the entity to be updated
   */
  void update(T entity);

  /**
   * Upserts (updates or inserts) a batch of entities in the underlying data store. If an entity
   * already exists, it will be updated; otherwise, it will be created.
   *
   * @param entities the list of entities to be upserted
   */
  void upsert(List<T> entities);
}
