package exchangerate.repository;

import exchangerate.model.ExchangeRate;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

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
      = "INSERT INTO exchangerates(basecurrencyid,targetcurrencyid,rate,date) VALUES(?,?,?,?) ";

  private static final String UPDATE_EXCHANGE_RATE
      = "UPDATE exchangerates SET rate = ? , date = ?   WHERE id = ?";


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
    ExchangeRate exchangeRate = null;
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_FIND_BY_ID)) {
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        exchangeRate = createEntity(resultSet);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return exchangeRate;
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
          BigDecimal.valueOf(resultSet.getDouble("rate")),
          resultSet.getDate("date").toLocalDate()
      );
    } catch (SQLException e) {
      return null;
    }
  }

  public Integer getByIdExchangeRate(Integer id) {
    Integer idExcnhageRate = null;
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_FIND_BY_ID_TARGERCURRENCY)) {

      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        idExcnhageRate = resultSet.getInt("id");

      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return idExcnhageRate;
  }
}
