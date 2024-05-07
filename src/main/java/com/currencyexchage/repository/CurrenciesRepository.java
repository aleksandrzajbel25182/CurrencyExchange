package com.currencyexchage.repository;

import com.currencyexchage.model.Currency;
import com.currencyexchage.model.CurrencyCB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;


public class CurrenciesRepository implements CrudRepository<Currency> {

  private final DataSource dataSource;

  private static final String GET_ALL_CURRENCIES = "SELECT id, code, fullName, sign FROM currencies ";
  private static final String GET_FIND_BY_ID = "SELECT id, code, fullName, sign FROM currencies WHERE id = ?";
  private static final String GET_FIND_BY_CODE = "SELECT id, code, fullName, sign FROM currencies WHERE code = ?";
  private static final String INSERT_CURRENCIES = "INSERT INTO currencies (code,fullname,sign) VALUES(?,?,?) ";

  public CurrenciesRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public List<Currency> get() {

    List<Currency> currencies = new ArrayList<>();

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_ALL_CURRENCIES)) {

      ResultSet resultSet = statement.executeQuery();

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


    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return currency;
  }

  public boolean findCode(String hasCode) {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_FIND_BY_CODE)) {

      statement.setString(1, hasCode);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        return true;
      } else {
        return false;
      }


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


    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return currency;
  }


  public void create(List<CurrencyCB> entity) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT_CURRENCIES)) {

      for (var currency : entity) {

        if (findCode(currency.getCharCode()) == false) {

          statement.setString(1, currency.getCharCode());
          statement.setString(2, currency.getName());
          statement.setString(3, currency.getNumCode());
          statement.addBatch();
        }


      }
      statement.executeBatch();

    } catch (SQLException e) {
     e.printStackTrace();
    }

  }

  @Override
  public void update(Currency entity) {

  }

  @Override
  public void save(Currency entity) {

  }

  @Override
  public void delete(Currency entity) {

  }

  @Override
  public Currency createEntity(ResultSet resultSet) {
    try {
      return new Currency(
          resultSet.getInt("id"),
          resultSet.getString("code"),
          resultSet.getString("fullname"),
          resultSet.getString("sign")
      );
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
