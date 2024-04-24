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

  private final String GET_ALL_EXCHANGE_RATE = "SELECT id,basecurrencyid, targetcurrencyid,rate FROM exchangerates";

  private static final String GET_FIND_BY_СODE = "SELECT id, basecurrencyid, targetcurrencyid, rate FROM exchangerates WHERE basecurrencyid = ? and targetcurrencyid =?";

  public ExchangeRateRepository(DataSource dataSource) {
    this.dataSource = dataSource;
    currenciesRepository = new CurrenciesRepository(dataSource);
  }

  @Override
  public List<ExchangeRate> get() {
    List<ExchangeRate> listExchange = new ArrayList<>();

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_ALL_EXCHANGE_RATE)) {

      statement.execute();

      ResultSet resultSet = statement.executeQuery();

      while (resultSet.next()) {
        var exchange = new ExchangeRate(
            resultSet.getInt("id"),
            currenciesRepository.findById(resultSet.getInt("basecurrencyid")),
            currenciesRepository.findById(resultSet.getInt("targetcurrencyid")),
            BigDecimal.valueOf(resultSet.getDouble("rate"))
        );

        listExchange.add(exchange);
      }

      return listExchange;

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ExchangeRate findById(int id) {
    return null;
  }

  public ExchangeRate finByCode(String baseCurrency, String targetCurrency) {
    ExchangeRate exchangeRate = null;
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_FIND_BY_СODE)) {

      var base = currenciesRepository.findByCode(baseCurrency);
      var target = currenciesRepository.findByCode(targetCurrency);

      statement.setInt(1, base.getId());
      statement.setInt(2, target.getId());

      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        exchangeRate = new ExchangeRate(
            resultSet.getInt("id"),
            currenciesRepository.findById(resultSet.getInt("basecurrencyid")),
            currenciesRepository.findById(resultSet.getInt("targetcurrencyid")),
            BigDecimal.valueOf(resultSet.getDouble("rate"))
        );

      }
    } catch (
        SQLException e) {
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
}
