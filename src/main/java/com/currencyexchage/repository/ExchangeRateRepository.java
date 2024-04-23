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
