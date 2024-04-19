package com.currencyexchage.repository;

import com.currencyexchage.model.Currency;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CurrenciesRepository implements CrudRepository<Currency> {

  private static final String GET_ALL_CURRENCIES = "SELECT id, code, fullName, sign FROM currencies ";

//  private ConnectionPool connectionPool;

//  public CurrenciesRepository(ConnectionPool connectionPool) {
//    this.connectionPool = connectionPool;
//  }
//  public CurrenciesRepository() {
//
//  }

  @Override
  public List<Currency> get() {

    List<Currency> currencies = new ArrayList<>();

//    connection = connectionPool.getConnection();
//    try (Connection connection = DatabaseConfig.getConnection();
//        PreparedStatement statement = connection.prepareStatement(GET_ALL_CURRENCIES)) {
//
//      ResultSet resultSet = statement.executeQuery();
//
//
//      while (resultSet.next()) {
//        var currency = new Currency();
//        currency.setId(resultSet.getInt("id"));
//        currency.setCode(resultSet.getString("code"));
//        currency.setFullName(resultSet.getString("fullName"));
//        currency.setSign(resultSet.getString("sign"));
//        currencies.add(currency);
//
//      }
    return currencies;
//
//    } catch (SQLException e) {
//      throw new RuntimeException(e);
//    } catch (ClassNotFoundException e) {
//      throw new RuntimeException(e);
//    }
  }

  @Override
  public Optional<Currency> findById(int id) {
    return Optional.empty();
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
