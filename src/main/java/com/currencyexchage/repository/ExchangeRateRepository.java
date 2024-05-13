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

  private static final String GET_ALL_EXCHANGE_RATE =
      "SELECT id,basecurrencyid, targetcurrencyid,rate "
          + "FROM exchangerates";

  private static final String GET_FIND_BY_CODE =
      "SELECT id, basecurrencyid, targetcurrencyid, rate "
          + "FROM exchangerates "
          + "WHERE basecurrencyid = ? and targetcurrencyid =?";

  private static final String INSERT_EXCHANGE_RATE = "INSERT INTO exchangerates(basecurrencyid,targetcurrencyid,rate) VALUES(?,?,?) ";

  private static final String UPDATE_EXCHANGE_RATE = "UPDATE exchangerates SET rate = ? WHERE id = ?";

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


  public void create(ExchangeRate entity) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EXCHANGE_RATE)) {

      preparedStatement.setInt(1, entity.getBaseCurrencyId().getId());
      preparedStatement.setInt(2, entity.getTargetCurrencyId().getId());
      preparedStatement.setBigDecimal(3, entity.getRate());
      preparedStatement.executeUpdate();


    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  @Override
  public void update(ExchangeRate entity) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_EXCHANGE_RATE)) {

      preparedStatement.setInt(1, entity.getId());
      preparedStatement.setBigDecimal(2, entity.getRate());
      preparedStatement.executeUpdate();


    } catch (SQLException e) {
      e.printStackTrace();
    }


  }

  public void createAll(List<ExchangeRate> entitys) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EXCHANGE_RATE)) {

      for (var entity : entitys) {

        preparedStatement.setInt(1, entity.getBaseCurrencyId().getId());
        preparedStatement.setInt(2, entity.getTargetCurrencyId().getId());
        preparedStatement.setBigDecimal(3, entity.getRate());
        preparedStatement.addBatch();
      }
      preparedStatement.executeBatch();


    } catch (SQLException e) {
      e.printStackTrace();
    }


  }

  @Override
  public void save(ExchangeRate entity) {

  }

  @Override
  public void delete(ExchangeRate entity) {

  }

  public boolean isEmpty() {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_ALL_EXCHANGE_RATE)) {

      ResultSet resultSet = statement.executeQuery();

      if (!resultSet.next()) {
        return true;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
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
