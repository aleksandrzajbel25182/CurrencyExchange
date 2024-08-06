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
import javax.sql.DataSource;


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
      = "UPDATE currencies "
      + "SET charcode = ? , fullname = ?  "
      + "WHERE id = ?";

  public CurrenciesRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void create(Currency entity) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT_CURRENCIES)) {
      statement.setString(1, entity.getCode());
      statement.setString(2, entity.getFullName());
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void createBatch(List<Currency> entities) {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CURRENCIES)) {
      for (Currency entity : entities) {
        preparedStatement.setString(1, entity.getCode());
        preparedStatement.setString(2, entity.getFullName());
        preparedStatement.addBatch();
      }
      preparedStatement.executeBatch();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

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

  @Override
  public Currency findById(int id) {
    Currency currency = new Currency();
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_FIND_BY_ID)) {
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        currency = createEntity(resultSet);
      }
      return currency;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void update(Currency entity) {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CURRENCY)) {
      preparedStatement.setString(1, entity.getCode());
      preparedStatement.setString(2, entity.getFullName());
      preparedStatement.setInt(3, entity.getId());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

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

  public Currency findByCode(String hasCode) {
    Currency currency = new Currency();
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_FIND_BY_CODE)) {

      statement.setString(1, hasCode);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        currency = createEntity(resultSet);
      }
      return currency;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public Currency createEntity(ResultSet resultSet) {
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
}
