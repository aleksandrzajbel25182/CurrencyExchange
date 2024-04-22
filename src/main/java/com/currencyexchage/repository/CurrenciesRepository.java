package com.currencyexchage.repository;

import com.currencyexchage.model.Currency;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentNavigableMap;
import javax.sql.DataSource;


public class CurrenciesRepository implements CrudRepository<Currency> {

  private final DataSource dataSource;

  private static final String GET_ALL_CURRENCIES = "SELECT id, code, fullName, sign FROM currencies ";
  private static final String GET_FIND_BY_ID = "SELECT id, code, fullName, sign FROM currencies WHERE id = ?";

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
        var currency = new Currency();
        currency.setId(resultSet.getInt("id"));
        currency.setCode(resultSet.getString("code"));
        currency.setFullName(resultSet.getString("fullName"));
        currency.setSign(resultSet.getString("sign"));

        currencies.add(currency);
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
      if(resultSet.next()){
        currency.setId(resultSet.getInt("id"));
        currency.setCode(resultSet.getString("code"));
        currency.setFullName(resultSet.getString("fullName"));
        currency.setSign(resultSet.getString("sign"));
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
}
