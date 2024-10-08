/*
 * SubscriptionsRepository.java        1.0 2024/08/15
 */
package com.repository;

import com.entities.Currency;
import com.entities.Subscription;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;
import javax.sql.DataSource;

/**
 * The SubscriptionsRepository class is responsible for managing the storage and retrieval of
 * subscription data in a database. It provides methods for creating, updating, and retrieving
 * subscriptions.
 *
 * @author Александр Зайбель
 * @version 1.0
 */
public class SubscriptionsRepository {

  private final DataSource dataSource;

  private final CurrenciesRepository currenciesRepository;

  private static final String GET_SUBSCRIPTIONS
      = "SELECT * "
      + "FROM subscriptions";
  private static final String GET_SUBSCRIPTIONS_STATUS
      = "SELECT * "
      + "FROM subscriptions "
      + "WHERE status IN(?)";

  private static final String GET_SUBSCRIPTIONS_FOR_UPDATE
      = "SELECT * FROM "
      + "subscriptions "
      + "WHERE status = ? FOR UPDATE LIMIT ?";

  /**
   * Constructs a new SubscriptionsRepository instance with the provided DataSource. It also
   * initializes a CurrenciesRepository instance.
   *
   * @param dataSource the DataSource to be used for database connections
   */
  public SubscriptionsRepository(DataSource dataSource) {
    this.dataSource = dataSource;
    currenciesRepository = new CurrenciesRepository(dataSource);
  }

  /**
   * Retrieves all subscriptions from the database.
   *
   * @return a list of all subscriptions
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  public List<Subscription> get() {
    List<Subscription> subscription = new ArrayList<>();
    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement()) {

      ResultSet resultSet = statement.executeQuery(GET_SUBSCRIPTIONS);
      while (resultSet.next()) {
        Optional<Currency> baseCurrency = currenciesRepository.findById(
            resultSet.getInt("basecurrencyid"));
        Optional<Currency> targetCurrency = currenciesRepository.findById(
            resultSet.getInt("targetcurrencyid"));
        subscription.add(
            new Subscription(
                resultSet.getInt("id"),
                resultSet.getString("url"),
                baseCurrency.get(),
                targetCurrency.get(),
                BigDecimal.valueOf(resultSet.getDouble("rate")),
                resultSet.getDate("date").toLocalDate(),
                resultSet.getString("status")));
      }
      return subscription;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves all subscriptions from the database with the status passed as a parameter.
   *
   * @return a list of subscriptions with the status
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  public List<Subscription> getByStatus(String status) {
    List<Subscription> subscriptions = new ArrayList<>();
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
            GET_SUBSCRIPTIONS_STATUS)) {
      preparedStatement.setString(1, status);

      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        Optional<Currency> baseCurrency = currenciesRepository.findById(
            resultSet.getInt("basecurrencyid"));
        Optional<Currency> targetCurrency = currenciesRepository.findById(
            resultSet.getInt("targetcurrencyid"));
        subscriptions.add(
            new Subscription(
                resultSet.getInt("id"),
                resultSet.getString("url"),
                baseCurrency.get(),
                targetCurrency.get(),
                BigDecimal.valueOf(resultSet.getDouble("rate")),
                resultSet.getDate("date").toLocalDate(),
                resultSet.getString("status")));
      }
      return subscriptions;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Subscription> getUnsentSubscriptionsWithLock(String status, int batchSize,
      Connection connection) {
    List<Subscription> subscriptions = new ArrayList<>();
    Connection connect = connection;
    PreparedStatement statement = null;
    try {
      statement = connect.prepareStatement(GET_SUBSCRIPTIONS_FOR_UPDATE);
      statement.setString(1, status);
      statement.setInt(2, batchSize);

      ResultSet resultSet = statement.executeQuery();

      while (resultSet.next()) {
        Optional<Currency> baseCurrency = currenciesRepository.findById(
            resultSet.getInt("basecurrencyid"));
        Optional<Currency> targetCurrency = currenciesRepository.findById(
            resultSet.getInt("targetcurrencyid"));

        subscriptions.add(
            new Subscription(
                resultSet.getInt("id"),
                resultSet.getString("url"),
                baseCurrency.get(),
                targetCurrency.get(),
                BigDecimal.valueOf(resultSet.getDouble("rate")),
                resultSet.getDate("date").toLocalDate(),
                resultSet.getString("status")));
      }
    } catch (SQLException e) {
      if (connect != null) {
        try {
          connect.rollback();
        } catch (SQLException ex) {
          throw new RuntimeException("Rollback failed", ex);
        }
      }
      throw new RuntimeException("Error in database operation", e);
    }

    return subscriptions;
  }

  /**
   * Upsert (inserts or updates) a subscription in the database. If the subscription with the same
   * URL, base currency, and target currency already exists, it updates the rate and date fields.
   * Otherwise, it inserts a new subscription.
   *
   * @param entity the subscription to be upserted
   * @throws RuntimeException if an SQL exception occurs during the database query
   */
  public void upsert(Subscription entity) {

    StringBuilder sql = new StringBuilder(
        "INSERT INTO subscriptions (url,basecurrencyid,targetcurrencyid,rate,date,status) "
            + "VALUES (?,?,?,?,?,?) "
            + "ON CONFLICT(url,basecurrencyid,targetcurrencyid) DO UPDATE "
            + "SET rate = EXCLUDED.rate, date = EXCLUDED.date, status = EXCLUDED.status");
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
      preparedStatement.setString(1, entity.getUrl());
      preparedStatement.setInt(2, entity.getBaseCurrencyId().getId());
      preparedStatement.setInt(3, entity.getTargetCurrencyId().getId());
      preparedStatement.setBigDecimal(4, entity.getRate());
      preparedStatement.setDate(5, Date.valueOf(entity.getDate()));
      preparedStatement.setString(6, entity.getStatus());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }


  public void upsert(List<Subscription> entities) {

    StringBuilder sql = new StringBuilder(
        "INSERT INTO subscriptions (url,basecurrencyid,targetcurrencyid,rate,date,status) "
            + "VALUES (?,?,?,?,?,?) "
            + "ON CONFLICT(url,basecurrencyid,targetcurrencyid) DO UPDATE "
            + "SET rate = EXCLUDED.rate, date = EXCLUDED.date, status = EXCLUDED.status");

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
      for (Subscription entry : entities) {
        preparedStatement.setString(1, entry.getUrl());
        preparedStatement.setInt(2, entry.getBaseCurrencyId().getId());
        preparedStatement.setInt(3, entry.getTargetCurrencyId().getId());
        preparedStatement.setBigDecimal(4, entry.getRate());
        preparedStatement.setDate(5, Date.valueOf(entry.getDate()));
        preparedStatement.setString(6, entry.getStatus());
        preparedStatement.addBatch();
      }
      preparedStatement.executeBatch();

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void upsert(List<Subscription> entities, Connection connection) {
    StringBuilder sql = new StringBuilder(
        "INSERT INTO subscriptions (url,basecurrencyid,targetcurrencyid,rate,date,status) "
            + "VALUES (?,?,?,?,?,?) "
            + "ON CONFLICT(url,basecurrencyid,targetcurrencyid) DO UPDATE "
            + "SET rate = EXCLUDED.rate, date = EXCLUDED.date, status = EXCLUDED.status");

    try {
      Connection conect = connection;
      PreparedStatement preparedStatement = conect.prepareStatement(sql.toString());
      for (Subscription entry : entities) {
        preparedStatement.setString(1, entry.getUrl());
        preparedStatement.setInt(2, entry.getBaseCurrencyId().getId());
        preparedStatement.setInt(3, entry.getTargetCurrencyId().getId());
        preparedStatement.setBigDecimal(4, entry.getRate());
        preparedStatement.setDate(5, Date.valueOf(entry.getDate()));
        preparedStatement.setString(6, entry.getStatus());
        preparedStatement.addBatch();
      }
      preparedStatement.executeBatch();

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}