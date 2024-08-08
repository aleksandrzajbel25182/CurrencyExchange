package com.repository;

import com.entities.Currency;
import com.entities.Subscriptions;
import com.interfaces.CrudRepository;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class SubscriptionsRepository {

  private final DataSource dataSource;

  private final CurrenciesRepository currenciesRepository;
  private static final String GET_SUBSCRIPTIONS
      = "SELECT * "
      + "FROM subscriptions";
  private static final String GET_SUBSCRIPTIONS_STATUS
      = "SELECT * "
      + "FROM subscriptions "
      + "WHERE status = \"не отправлено\" ";

  private static final String INSERT_SUBSCRIPTIONS
      = "INSERT INTO subscriptions (url,basecurrencyid,targetcurrencyid,rate,date,status) "
      + "VALUES(?,?,?,?,?,?) ";

  public SubscriptionsRepository(DataSource dataSource) {
    this.dataSource = dataSource;
    currenciesRepository = new CurrenciesRepository(dataSource);
  }

  public void upsert(Subscriptions entity) {

    StringBuilder sql = new StringBuilder(
        "INSERT INTO subscriptions (url,basecurrencyid,targetcurrencyid,rate,date,status) "
            + "VALUES (?,?,?,?,?,?) "
            + "ON CONFLICT(url,basecurrencyid,targetcurrencyid) DO UPDATE "
            + "SET rate = EXCLUDED.rate, date = EXCLUDED.date");
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

  public List<Subscriptions> get() {
    List<Subscriptions> subscriptions = new ArrayList<>();
    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement()) {

      ResultSet resultSet = statement.executeQuery(GET_SUBSCRIPTIONS);
      while (resultSet.next()) {
        subscriptions.add(
            new Subscriptions(
                resultSet.getInt("id"),
                resultSet.getString("url"),
                currenciesRepository.findById(resultSet.getInt("basecurrencyid")),
                currenciesRepository.findById(resultSet.getInt("targetcurrencyid")),
                BigDecimal.valueOf(resultSet.getDouble("rate")),
                resultSet.getDate("date").toLocalDate(),
                resultSet.getString("status")));
      }
      return subscriptions;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Subscriptions> getByStatus() {
    List<Subscriptions> subscriptions = new ArrayList<>();
    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement()) {

      ResultSet resultSet = statement.executeQuery(GET_SUBSCRIPTIONS_STATUS);
      while (resultSet.next()) {
        subscriptions.add(
            new Subscriptions(
                resultSet.getInt("id"),
                resultSet.getString("url"),
                currenciesRepository.findById(resultSet.getInt("basecurrencyid")),
                currenciesRepository.findById(resultSet.getInt("targetcurrencyid")),
                BigDecimal.valueOf(resultSet.getDouble("rate")),
                resultSet.getDate("date").toLocalDate(),
                resultSet.getString("status")));
      }
      return subscriptions;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
