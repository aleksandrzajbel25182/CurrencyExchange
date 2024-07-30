package exchangerate.repository;

import exchangerate.model.ExchangeRate;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import javafx.util.Pair;


public class ExchangeRateRepository implements CrudRepository<ExchangeRate> {

  private final DataSource dataSource;
  private CurrenciesRepository currenciesRepository;

  private static final String GET_ALL_EXCHANGE_RATE
      = "SELECT id,basecurrencyid, targetcurrencyid,rate,date "
      + "FROM exchangerates";

  private static final String GET_FIND_BY_CODE
      = "SELECT id, basecurrencyid, targetcurrencyid, rate, date "
      + "FROM exchangerates "
      + "WHERE basecurrencyid = ? and targetcurrencyid =?";

  private static final String GET_FIND_BY_ID
      = "SELECT id,basecurrencyid, targetcurrencyid,rate,date "
      + "FROM exchangerates "
      + "WHERE id = ?";
  public static final String GET_ID_BY_DATE
      = "SELECT id,basecurrencyid, targetcurrencyid,rate,date"
      + "FROM exchangerates"
      + "WHERE targetcurrencyid = ? and date = ?";

  private static final String GET_FIND_BY_ID_TARGERCURRENCY
      = "SELECT id "
      + "FROM exchangerates "
      + "WHERE targetcurrencyid = ? ";
  private static final String INSERT_EXCHANGE_RATE
      = "INSERT INTO exchangerates(basecurrencyid,targetcurrencyid,rate,date) "
      + "VALUES(?,?,?,?) ";

  private static final String UPDATE_EXCHANGE_RATE
      = "UPDATE exchangerates SET rate = ? , date = ? "
      + "WHERE id = ?";

  public ExchangeRateRepository(DataSource dataSource) {
    this.dataSource = dataSource;
    currenciesRepository = new CurrenciesRepository(dataSource);
  }

  @Override
  public List<ExchangeRate> get() {
    List<ExchangeRate> listExchange;

    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement()) {
      ResultSet resultSet = statement.executeQuery(GET_ALL_EXCHANGE_RATE);
      listExchange = new ArrayList<>();
      while (resultSet.next()) {
        listExchange.add(createEntity(resultSet));
      }
      return listExchange;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ExchangeRate findById(int id) {
    ExchangeRate exchangeRate = null;
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_FIND_BY_ID)) {
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        exchangeRate = createEntity(resultSet);
      }
      return exchangeRate;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
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
      return exchangeRate;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public ExchangeRate findByDate(Integer targetCurrencyId, Date date) {
    ExchangeRate exchangeRate = null;
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_ID_BY_DATE)) {

      var target = currenciesRepository.findById(targetCurrencyId);

      statement.setInt(1, target.getId());
      statement.setDate(2, date);

      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        exchangeRate = createEntity(resultSet);
      }
      return exchangeRate;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void create(ExchangeRate entity) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EXCHANGE_RATE)) {

      preparedStatement.setInt(1, entity.getBaseCurrencyId().getId());
      preparedStatement.setInt(2, entity.getTargetCurrencyId().getId());
      preparedStatement.setBigDecimal(3, entity.getRate());
      preparedStatement.setDate(4, Date.valueOf(entity.getDate()));
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void createBatch(List<ExchangeRate> entities) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EXCHANGE_RATE)) {
      for (ExchangeRate entity : entities) {
        preparedStatement.setInt(1, entity.getBaseCurrencyId().getId());
        preparedStatement.setInt(2, entity.getTargetCurrencyId().getId());
        preparedStatement.setBigDecimal(3, entity.getRate());
        preparedStatement.setDate(4, Date.valueOf(entity.getDate()));
        preparedStatement.addBatch();
      }
      preparedStatement.executeBatch();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void update(ExchangeRate entity) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_EXCHANGE_RATE)) {

      preparedStatement.setBigDecimal(1, entity.getRate());
      preparedStatement.setDate(2, Date.valueOf(entity.getDate()));
      preparedStatement.setInt(3, entity.getId());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void updateBatch(List<ExchangeRate> entities) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_EXCHANGE_RATE)) {
      for (ExchangeRate entity : entities) {
        preparedStatement.setBigDecimal(1, entity.getRate());
        preparedStatement.setDate(2, Date.valueOf(entity.getDate()));
        preparedStatement.setInt(3, entity.getId());
        preparedStatement.executeBatch();
      }
      preparedStatement.executeBatch();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  @Override
  public ExchangeRate createEntity(ResultSet resultSet) {
    try {
      return new ExchangeRate(
          resultSet.getInt("id"),
          currenciesRepository.findById(resultSet.getInt("basecurrencyid")),
          currenciesRepository.findById(resultSet.getInt("targetcurrencyid")),
          BigDecimal.valueOf(resultSet.getDouble("rate")),
          resultSet.getDate("date").toLocalDate()
      );
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public Map<Pair<Integer, Integer>, Integer> getExchangeRateIdsByPairs(
      List<Pair<Integer, Integer>> listCharCodesPairs) {
    Map<Pair<Integer, Integer>, Integer> existingPairs = new HashMap<>();

    StringBuilder sql = new StringBuilder("SELECT id,basecurrencyid,targetcurrencyid "
        + "FROM exchangerates "
        + "WHERE (basecurrencyid, targetcurrencyid) "
        + "IN(");

    String[] arrayOfStrings = new String[listCharCodesPairs.size()];
    Arrays.fill(arrayOfStrings, "?");
    sql.append(String.join(", ", arrayOfStrings));
    sql.append(")");
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {

      int parameterIndex = 1;
      for (Pair<Integer, Integer> pairs : listCharCodesPairs) {
        preparedStatement.setInt(parameterIndex++, pairs.getKey());
        preparedStatement.setInt(parameterIndex++, pairs.getValue());
      }
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          int id = resultSet.getInt("id");
          int baseCharCode = resultSet.getInt("basecurrencyid");
          int targetCharCode = resultSet.getInt("targetcurrencyid");
          existingPairs.put(new Pair<>(baseCharCode, targetCharCode), id);
        }
        return existingPairs;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
