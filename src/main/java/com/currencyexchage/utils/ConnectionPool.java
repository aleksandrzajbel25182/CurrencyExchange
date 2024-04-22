package com.currencyexchage.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

  private static HikariConfig config = new HikariConfig();
  private static final HikariDataSource dataSource;

  static {
    try {
      Class.forName("org.postgresql.Driver");
      config.setJdbcUrl("jdbc:postgresql://pgsql:5432/currencyExchangedb");
      config.setUsername("postgres");
      config.setPassword("root");
      config.setConnectionTimeout(50000);
      config.setMaximumPoolSize(10);

      dataSource = new HikariDataSource(config);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public static Connection getConnection() {
    try {
      return dataSource.getConnection();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public static HikariDataSource getDataSource() {
    return dataSource;
  }
}
