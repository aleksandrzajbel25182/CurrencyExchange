package com.interfaces;

import java.util.List;


public interface CrudRepository<T> {

  void create(T entity);

  void createBatch(List<T> entities);

  List<T> get();

  T findById(int id);

  void update(T entity);

  void upsert(List<T> entities);
}
