/*
 * ExchangeRateRepository.java        1.0 2024/08/15
 */
package com.repository;

import com.entities.ExchangeRate;
import com.interfaces.CrudRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.util.Pair;

import javax.sql.DataSource;

/**
 * The `ExchangeRateRepository` class is an implementation of the {@link CrudRepository} interface
 * that provides CRUD operations for managing exchange rates in a database. The class also uses a
 * {@link CurrenciesRepository} instance to retrieve currency information
 *
 * @author Александр Зайбель
 * @version 1.0
 * @see CrudRepository
 */
public class ExchangeRateRepository implements CrudRepository<ExchangeRate> {

  private final DataSource dataSource;

  private CurrenciesRepository currenciesRepository;

  private static final String GET_ALL_EXCHANGE_RATE
      = "SELECT id,basecurrencyid, targetcurrencyid,rate,date "
      + "FROM exchangerates";

  private static final String GET_FIND_BY_CODE
      = "SELECT id, basecurrencyid, targetcurrencyid, rate, date "
      + "FROM exchangerates "
      + "WHERE basecurrencyid = ? and targetcurrencyid =?";

  private static final String GET_FIND_BY_ID
      = "SELECT id,basecurrencyid, targetcurrencyid,rate,date "
      + "FROM exchangerates "
      + "WHERE id = ?";

  private static final String INSERT_EXCHANGE_RATE
      = "INSERT INTO exchangerates(basecurrencyid,targetcurrencyid,rate,date) "
      + "VALUES(?,?,?,?) ";

  private static final String UPDATE_EXCHANGE_RATE
      = "UPDATE exchangerates SET rate = ? , date = ? "
      + "WHERE id = ?";

  /**
   * Constructs a new `ExchangeRateRepository` instance with the provided `DataSource`.
   *
   * @param dataSource the `DataSource` to be used for database operations
   */
  public ExchangeRateRepository(DataSource dataSource) {
    this.dataSource = dataSource;
    currenciesRepository = new CurrenciesRepository(dataSource);
  }

  /**
   * Creates a new `ExchangeRate` entity in the database.
   *
   * @param entity the `ExchangeRate` entity to be created
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  @Override
  public void create(ExchangeRate entity) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EXCHANGE_RATE)) {

      preparedStatement.setInt(1, entity.getBaseCurrencyId().getId());
      preparedStatement.setInt(2, entity.getTargetCurrencyId().getId());
      preparedStatement.setBigDecimal(3, entity.getRate());
      preparedStatement.setDate(4, Date.valueOf(entity.getDate()));
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates entities in the database. The batch processing of the driver is used
   *
   * @param entities - list of "ExchangeRate" objects to be created.
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  @Override
  public void createBatch(List<ExchangeRate> entities) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EXCHANGE_RATE)) {
      for (ExchangeRate entity : entities) {
        preparedStatement.setInt(1, entity.getBaseCurrencyId().getId());
        preparedStatement.setInt(2, entity.getTargetCurrencyId().getId());
        preparedStatement.setBigDecimal(3, entity.getRate());
        preparedStatement.setDate(4, Date.valueOf(entity.getDate()));
        preparedStatement.addBatch();
      }
      preparedStatement.executeBatch();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Retrieves all `ExchangeRate` entities from the database.
   *
   * @return a list of all `Exchange Rate` entities
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  @Override
  public List<ExchangeRate> get() {
    List<ExchangeRate> listExchange;

    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement()) {
      ResultSet resultSet = statement.executeQuery(GET_ALL_EXCHANGE_RATE);
      listExchange = new ArrayList<>();
      while (resultSet.next()) {
        listExchange.add(createEntity(resultSet));
      }
      return listExchange;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Finds a `ExchangeRate` entity by its ID.
   *
   * @param id the ID of the `Currency` entity to be found
   * @return the `ExchangeRate` entity with the specified ID
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  @Override
  public ExchangeRate findById(int id) {
    ExchangeRate exchangeRate = null;
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_FIND_BY_ID)) {
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        exchangeRate = createEntity(resultSet);
      }
      return exchangeRate;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Updates an existing `ExchangeRate` entity in the database.
   *
   * @param entity the `ExchangeRate` entity to be updated
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  @Override
  public void update(ExchangeRate entity) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_EXCHANGE_RATE)) {

      preparedStatement.setBigDecimal(1, entity.getRate());
      preparedStatement.setDate(2, Date.valueOf(entity.getDate()));
      preparedStatement.setInt(3, entity.getId());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Performs an upsert operation on a list of `ExchangeRate` entities. If a `ExchangeRate` entity
   * with the same code already exists, it will be updated; otherwise, it will be inserted.
   *
   * @param entities the list of `ExchangeRate` entities to be upserted
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  @Override
  public void upsert(List<ExchangeRate> entities) {
    StringBuilder sql = new StringBuilder(
        "INSERT INTO exchangerates (basecurrencyid,targetcurrencyid,rate,date) "
            + "VALUES (?,?,?,?) "
            + "ON CONFLICT(basecurrencyid,targetcurrencyid,date) DO UPDATE "
            + "SET rate = EXCLUDED.rate, date = EXCLUDED.date");
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {

      for (ExchangeRate entity : entities) {
        preparedStatement.setInt(1, entity.getBaseCurrencyId().getId());
        preparedStatement.setInt(2, entity.getTargetCurrencyId().getId());
        preparedStatement.setBigDecimal(3, entity.getRate());
        preparedStatement.setDate(4, Date.valueOf(entity.getDate()));
        preparedStatement.addBatch();
      }
      preparedStatement.executeBatch();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves an exchange rate by the base and target currency codes.
   *
   * @param baseCurrency   base currency codes
   * @param targetCurrency target currency codes
   * @return an `Optional` containing the `ExchangeRate` entity with the specified code, or an empty
   * `Optional` if not found
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  public Optional<ExchangeRate> finByCode(String baseCurrency, String targetCurrency) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_FIND_BY_CODE)) {

      var base = currenciesRepository.findByCode(baseCurrency);
      var target = currenciesRepository.findByCode(targetCurrency);

      if (!base.isPresent() || !target.isPresent()) {
        return Optional.empty();
      }
      statement.setInt(1, base.get().getId());
      statement.setInt(2, target.get().getId());
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
   * Updates a batch of exchange rates in the database.The batch processing of the driver is used
   *
   * @param entities the `ExchangeRate` list to be updated
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  public void updateBatch(List<ExchangeRate> entities) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_EXCHANGE_RATE)) {
      for (ExchangeRate entity : entities) {
        preparedStatement.setBigDecimal(1, entity.getRate());
        preparedStatement.setDate(2, Date.valueOf(entity.getDate()));
        preparedStatement.setInt(3, entity.getId());
        preparedStatement.executeBatch();
      }
      preparedStatement.executeBatch();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Creates a `ExchangeRate` entity from a `ResultSet`.
   *
   * @param resultSet the `ResultSet` containing the data for the `ExchangeRate` entity
   * @return the `ExchangeRate` entity created from the `ResultSet`
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  private ExchangeRate createEntity(ResultSet resultSet) {
    try {
      return new ExchangeRate(
          resultSet.getInt("id"),
          currenciesRepository.findById(resultSet.getInt("basecurrencyid")),
          currenciesRepository.findById(resultSet.getInt("targetcurrencyid")),
          BigDecimal.valueOf(resultSet.getDouble("rate")),
          resultSet.getDate("date").toLocalDate()
      );
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves the exchange rate IDs for the given list of currency code pairs.
   *
   * @param listCharCodesPairs a list of currency code pairs represented as
   *                           {@link Pair<Integer, Integer>}
   * @return a  {@code Map<Pair<Integer, Integer>, Integer>}, where the key is the currency code
   * pair and the value is the corresponding exchange rate ID
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  public Map<Pair<Integer, Integer>, Integer> getExchangeRateIdsByPairs(
      List<Pair<Integer, Integer>> listCharCodesPairs) {
    Map<Pair<Integer, Integer>, Integer> existingPairs = new HashMap<>();

    StringBuilder sql = new StringBuilder("SELECT id,basecurrencyid,targetcurrencyid "
        + "FROM exchangerates "
        + "WHERE (basecurrencyid, targetcurrencyid) "
        + "IN(");

    String[] arrayOfStrings = new String[listCharCodesPairs.size()];
    Arrays.fill(arrayOfStrings, "(?,?)");
    sql.append(String.join(", ", arrayOfStrings));
    sql.append(")");
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {

      int parameterIndex = 1;
      for (Pair<Integer, Integer> pairs : listCharCodesPairs) {
        preparedStatement.setInt(parameterIndex++, pairs.getKey());
        preparedStatement.setInt(parameterIndex++, pairs.getValue());
      }
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          int id = resultSet.getInt("id");
          int baseCharCode = resultSet.getInt("basecurrencyid");
          int targetCharCode = resultSet.getInt("targetcurrencyid");
          existingPairs.put(new Pair<>(baseCharCode, targetCharCode), id);
        }
        return existingPairs;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
