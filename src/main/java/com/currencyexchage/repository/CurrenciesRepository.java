package com.currencyexchage.repository;

import com.currencyexchage.model.Currencies;
import com.currencyexchage.utils.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrenciesRepository implements CrudRepository<Currencies> {

  private static final String GET_ALL_CURRENCIES = "SELECT id, code, fullName, sign FROM currencyexchanger.currencies ";

  @Override
  public List<Currencies> get() {

    List<Currencies> currencies = new ArrayList<>();
    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_ALL_CURRENCIES)) {

      ResultSet resultSet = statement.getResultSet();
      Currencies currency = new Currencies();

      while (resultSet.next()) {
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
  public Optional<Currencies> findById(int id) {
    return Optional.empty();
  }

  @Override
  public void update(Currencies entity) {

  }

  @Override
  public void save(Currencies entity) {

  }

  @Override
  public void delete(Currencies entity) {

  }
}
