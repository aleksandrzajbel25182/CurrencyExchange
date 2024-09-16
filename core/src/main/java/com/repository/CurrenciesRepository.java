/*
 * CurrenciesRepository.java        1.0 2024/08/14
 */
package com.repository;

import com.entities.Currency;
import com.interfaces.CrudRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

/**
 * The `CurrenciesRepository` class is an implementation of the {@link CrudRepository} interface,
 * which provides CRUD operations for the `Currency` entity. This class uses a `DataSource` to
 * interact with a database and perform various operations on the `currencies` table.
 *
 * @author Александр Зайбель
 * @version 1.0
 * @see CrudRepository
 */
public class CurrenciesRepository implements CrudRepository<Currency> {

  private final DataSource dataSource;

  private static final String GET_ALL_CURRENCIES
      = "SELECT id, charcode, fullName "
      + "FROM currencies ";

  private static final String GET_FIND_BY_ID
      = "SELECT id, charcode, fullName "
      + "FROM currencies "
      + "WHERE id = ?";

  private static final String GET_FIND_BY_CODE
      = "SELECT id, charcode,fullName "
      + "FROM currencies "
      + "WHERE charcode = ?";

  private static final String INSERT_CURRENCIES
      = "INSERT INTO currencies (charcode,fullname) "
      + "VALUES(?,?) ";

  private static final String UPDATE_CURRENCY
      = "INSERT INTO currencies (charcode,fullname) "
      + "VALUES (?,?) "
      + "ON CONFLICT(charcode) DO UPDATE "
      + "SET charcode = EXCLUDED.charcode, fullname = EXCLUDED.fullname";

  /**
   * Constructs a new `CurrenciesRepository` instance with the provided `DataSource`.
   *
   * @param dataSource the `DataSource` to be used for database operations
   */
  public CurrenciesRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Creates a new `Currency` entity in the database.
   *
   * @param entity the `Currency` entity to be created
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  @Override
  public Currency create(Currency entity) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT_CURRENCIES,
            Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, entity.getCode());
      statement.setString(2, entity.getFullName());
      statement.executeUpdate();
      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          entity.setId(generatedKeys.getInt(1));
        } else {
          throw new SQLException("Creating currency failed, no ID obtained.");
        }
      }

      return entity;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Creates entities in the database. The batch processing of the driver is used
   *
   * @param entities - list of "Currency" objects to be created.
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  @Override
  public List<Currency> create(List<Currency> entities) {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CURRENCIES,
            Statement.RETURN_GENERATED_KEYS)) {
      for (Currency entity : entities) {
        preparedStatement.setString(1, entity.getCode());
        preparedStatement.setString(2, entity.getFullName());
        preparedStatement.addBatch();
      }
      preparedStatement.executeBatch();
      try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
        int i = 0;
        while (generatedKeys.next()) {
          int id = generatedKeys.getInt(1);
          entities.get(i).setId(id);
          i++;
        }
      }
      return entities;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves all `Currency` entities from the database.
   *
   * @return a list of all `Currency` entities
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  @Override
  public List<Currency> get() {
    List<Currency> currencies = new ArrayList<>();
    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement()) {
      ResultSet resultSet = statement.executeQuery(GET_ALL_CURRENCIES);
      while (resultSet.next()) {
        currencies.add(createEntity(resultSet));
      }
      return currencies;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Finds a `Currency` entity by its ID.
   *
   * @param id the ID of the `Currency` entity to be found
   * @return the `Currency` entity with the specified ID
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  @Override
  public Optional<Currency> findById(int id) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_FIND_BY_ID)) {
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();
      if (!resultSet.next()) {
        return Optional.empty();
      }
      return Optional.of(createEntity(resultSet));

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Updates an existing `Currency` entity in the database.
   *
   * @param entity the `Currency` entity to be updated
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  @Override
  public void upsert(Currency entity) {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CURRENCY)) {
      preparedStatement.setString(1, entity.getCode());
      preparedStatement.setString(2, entity.getFullName());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Performs an upsert operation on a list of `Currency` entities. If a `Currency` entity with the
   * same code already exists, it will be updated; otherwise, it will be inserted.
   *
   * @param entities the list of `Currency` entities to be upserted
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  @Override
  public void upsert(List<Currency> entities) {
    StringBuilder sql = new StringBuilder(
        "INSERT INTO currencies (charcode,fullname) "
            + "VALUES (?,?) "
            + "ON CONFLICT(charcode) DO UPDATE "
            + "SET charcode = EXCLUDED.charcode, fullname = EXCLUDED.fullname");
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {

      for (Currency currency : entities) {
        preparedStatement.setString(1, currency.getCode());
        preparedStatement.setString(2, currency.getFullName());
        preparedStatement.addBatch();
      }
      preparedStatement.executeBatch();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Finds a `Currency` entity by its code.
   *
   * @param hasCode the code of the `Currency` entity to be found
   * @return an `Optional` containing the `Currency` entity with the specified code, or an empty
   * `Optional` if not found
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  public Optional<Currency> findByCode(String hasCode) {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_FIND_BY_CODE)) {

      statement.setString(1, hasCode);
      ResultSet resultSet = statement.executeQuery();
      if (!resultSet.next()) {
        return Optional.empty();
      }
      return Optional.of(createEntity(resultSet));

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves a map of currency IDs by their codes for the provided collection of codes.
   *
   * @param collection the collection of currency codes
   * @return a `HashMap` mapping currency codes to their corresponding IDs
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  public HashMap<String, Integer> getCurrenciesIdByCode(Collection<?> collection) {
    HashMap<String, Integer> idByCodeMap = new HashMap<>();
    StringBuilder sql = new StringBuilder(
        "SELECT id, charcode FROM currencies WHERE charcode IN (");

    String[] arrayOfStrings = new String[collection.size()];
    Arrays.fill(arrayOfStrings, "?");
    sql.append(String.join(", ", arrayOfStrings));
    sql.append(")");

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql.toString())) {
      int i = 1;
      for (Object item : collection) {
        statement.setString(i, item.toString());
        i++;
      }
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        idByCodeMap.put(
            resultSet.getString("charcode"),
            resultSet.getInt("id"));

      }
      return idByCodeMap;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Creates a `Currency` entity from a `ResultSet`.
   *
   * @param resultSet the `ResultSet` containing the data for the `Currency` entity
   * @return the `Currency` entity created from the `ResultSet`
   * @throws SQLException if an error occurs while accessing the `ResultSet`
   */
  private Currency createEntity(ResultSet resultSet) {
    try {
      return new Currency(
          resultSet.getInt("id"),
          resultSet.getString("charcode"),
          resultSet.getString("fullname")
      );
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
