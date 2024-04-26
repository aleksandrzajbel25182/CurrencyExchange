package com.currencyexchage.repository;

import com.currencyexchage.model.ExchangeRate;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class ExchangeRateRepository implements CrudRepository<ExchangeRate> {

  private final DataSource dataSource;
  private CurrenciesRepository currenciesRepository;

  private static final String GET_ALL_EXCHANGE_RATE = "SELECT id,basecurrencyid, targetcurrencyid,rate "
      + "FROM exchangerates";

  private static final String GET_FIND_BY_CODE = "SELECT id, basecurrencyid, targetcurrencyid, rate "
      + "FROM exchangerates "
      + "WHERE basecurrencyid = ? and targetcurrencyid =?";

  public ExchangeRateRepository(DataSource dataSource) {
    this.dataSource = dataSource;
    currenciesRepository = new CurrenciesRepository(dataSource);
  }

  @Override
  public List<ExchangeRate> get() {
    List<ExchangeRate> listExchange;

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_ALL_EXCHANGE_RATE)) {

      ResultSet resultSet = statement.executeQuery();
      listExchange = new ArrayList<>();

      while (resultSet.next()) {
        listExchange.add(createEntity(resultSet));
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return listExchange;
  }

  @Override
  public ExchangeRate findById(int id) {
    return null;
  }

  public ExchangeRate finByCode(String baseCurrency, String targetCurrency) {
    ExchangeRate exchangeRate = null;
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_FIND_BY_CODE)) {

      var base = currenciesRepository.findByCode(baseCurrency);
      var target = currenciesRepository.findByCode(targetCurrency);

      statement.setInt(1, base.getId());
      statement.setInt(2, target.getId());

      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        exchangeRate = createEntity(resultSet);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return exchangeRate;
  }

  @Override
  public void update(ExchangeRate entity) {

  }

  @Override
  public void save(ExchangeRate entity) {

  }

  @Override
  public void delete(ExchangeRate entity) {

  }

  @Override
  public ExchangeRate createEntity(ResultSet resultSet) {
    try {
      return new ExchangeRate(
          resultSet.getInt("id"),
          currenciesRepository.findById(resultSet.getInt("basecurrencyid")),
          currenciesRepository.findById(resultSet.getInt("targetcurrencyid")),
          BigDecimal.valueOf(resultSet.getDouble("rate"))
      );
    } catch (SQLException e) {
      return null;
    }
  }
}
