package com.currencyexchage.repository;

import java.sql.ResultSet;
import java.util.List;


public interface CrudRepository<T> {

  List<T> get();

  T findById(int id);

  void update(T entity);

  void save(T entity);

  void delete(T entity);

  T createEntity(ResultSet resultSet);
}
