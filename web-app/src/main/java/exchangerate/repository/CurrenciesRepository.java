package exchangerate.repository;

import exchangerate.model.Currency;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;


public class CurrenciesRepository implements CrudRepository<Currency> {

  private final DataSource dataSource;

  private static final String GET_ALL_CURRENCIES = "SELECT id, charcode, fullName FROM currencies ";
  private static final String GET_FIND_BY_ID = "SELECT id, charcode, fullName FROM currencies WHERE id = ?";
  private static final String GET_FIND_BY_CODE = "SELECT id, charcode,fullName FROM currencies WHERE charcode = ?";

  private static final String INSERT_CURRENCIES = "INSERT INTO currencies (charcode,fullname) VALUES(?,?) ";

  private static final String UPDATE_CURRENCY = "UPDATE currencies SET charcode = ? , fullname = ?  WHERE id = ?";


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
        currencies.add(createEntity(resultSet));
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
        currency = createEntity(resultSet);
      }


    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return currency;
  }

  public HashMap<String, Integer> getCurrenciesIdByCode(Collection<?> collection) {
    HashMap<String, Integer> idByCodeMap = new HashMap<>();
    StringBuilder sql = new StringBuilder(
        "SELECT id, charcode FROM currencies WHERE charcode IN (");

    for (int i = 0; i < collection.size(); i++) {
      if (i > 0) {
        sql.append(", ");
      }
      sql.append("?");
    }
    sql.append(")");

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql.toString())) {

      int i = 1;
      for (Object item : collection) {
        statement.setString(i, item.toString());
        i++;
      }

      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        idByCodeMap.put(
            resultSet.getString("charcode"),
            resultSet.getInt("id"));

      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return idByCodeMap;
  }

  public Currency findByCode(String hasCode) {
    Currency currency = new Currency();
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_FIND_BY_CODE)) {

      statement.setString(1, hasCode);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        currency = createEntity(resultSet);
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return currency;
  }


  public void create(Currency entity) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT_CURRENCIES)) {
      statement.setString(1, entity.getCode());
      statement.setString(2, entity.getFullName());
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void createBatch(List<Currency> entities) {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CURRENCIES)) {
      for (Currency entity : entities) {
        preparedStatement.setString(1, entity.getCode());
        preparedStatement.setString(2, entity.getFullName());
        preparedStatement.addBatch();
      }
      preparedStatement.executeBatch();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  @Override
  public void update(Currency entity) {

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CURRENCY)) {
      preparedStatement.setString(1, entity.getCode());
      preparedStatement.setString(2, entity.getFullName());
      preparedStatement.setInt(3, entity.getId());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Currency createEntity(ResultSet resultSet) {
    try {
      return new Currency(
          resultSet.getInt("id"),
          resultSet.getString("charcode"),
          resultSet.getString("fullname")
      );
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
