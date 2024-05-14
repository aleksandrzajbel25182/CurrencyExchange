package com.currencyexchage.repository;

import com.currencyexchage.model.CurrentDate;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

public class CurrentDateRepository {

  private final DataSource dataSource;

  private static final String INSERT_CURRENT_DATE = "INSERT INTO data_central_bank (currentdate) VALUES(?) ";
  private static final String UPDATE_CURRENT_DATE = "UPDATE data_central_bank SET currentdate = ? ";
  private static final String COINCIDENCE = "SELECT currentdate FROM data_central_bank WHERE currentdate = ?";
//  private static final String GET = "SELECT currentdate FROM data_central_bank";


  public CurrentDateRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }


  public boolean get(Date isDate) {
    java.sql.Date sqlDate = new java.sql.Date(isDate.getTime());
    CurrentDate currentDate = new CurrentDate();
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(COINCIDENCE)) {

      statement.setDate(1, sqlDate);
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
//        currentDate.setCurrentDate(resultSet.getDate("currentdate"));
        connection.close();
        return true;
      }

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
    return false;
  }

//  public boolean isEmptyData() {
//
//    try {
//      Connection connection = dataSource.getConnection();
//      PreparedStatement statement = connection.prepareStatement(GET);
//
//      ResultSet resultSet = statement.executeQuery();
//
//      if (!resultSet.next()) {
//        return true;
//      }
//
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
//    return false;
//  }

  public void create(CurrentDate currentDate) {
    try {
      Connection connection = dataSource.getConnection();
      PreparedStatement statement = connection.prepareStatement(INSERT_CURRENT_DATE);

      statement.setDate(1, convertDate(currentDate.getCurrentDate()));
      statement.executeUpdate();

      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  public void update(CurrentDate currentDate) {
    try {
      Connection connection = dataSource.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CURRENT_DATE);

      preparedStatement.setDate(1, convertDate(currentDate.getCurrentDate()));
      preparedStatement.executeUpdate();

      connection.close();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private java.sql.Date convertDate(Date date) {
    return new java.sql.Date(date.getTime());
  }

}
