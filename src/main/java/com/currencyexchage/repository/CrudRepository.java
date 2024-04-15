package com.currencyexchage.repository;

import java.util.Optional;

public interface CrudRepository<T> {
  T get ();
  Optional<T> findById(int id);
  void update(T entity);
  void save(T entity);
  void delete(T entity);
}
