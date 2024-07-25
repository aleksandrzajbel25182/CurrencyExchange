package exchangerate.repository;

import java.sql.ResultSet;
import java.util.List;


public interface CrudRepository<T> {

  List<T> get();

  T findById(int id);

  void update(T entity);

  T createEntity(ResultSet resultSet);

  void createBatch(List<T> entity);
}
