package com.currencyexchage.repository;

import com.currencyexchage.model.Currency;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;


public class CurrenciesRepository implements CrudRepository<Currency> {

  private final DataSource dataSource;

  private static final String GET_ALL_CURRENCIES = "SELECT id, code, fullName, sign FROM currencies ";
  private static final String GET_FIND_BY_ID = "SELECT id, code, fullName, sign FROM currencies WHERE id = ?";
  private static final String GET_FIND_BY_СODE = "SELECT id, code, fullName, sign FROM currencies WHERE code = ?";

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
        currencies.add(createCurrency(resultSet));
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
        currency= createCurrency(resultSet);
      }


    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return currency;
  }

  public Currency findByCode(String hasСode) {
    Currency currency = new Currency();
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_FIND_BY_СODE)) {

      statement.setString(1, hasСode);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        currency = createCurrency(resultSet);
      }


    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return currency;
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

  private Currency createCurrency(ResultSet resultSet) {
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
