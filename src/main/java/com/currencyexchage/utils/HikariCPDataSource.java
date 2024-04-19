package com.currencyexchage.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class HikariCPDataSource {

  private static HikariConfig config = new HikariConfig();
  private static HikariDataSource ds;

  static {
    config.setJdbcUrl("jdbc:postgresql://pgsql:5432/currencyExchangedb");
    config.setUsername("postgres");
    config.setPassword("root");
    config.setConnectionTimeout(50000);
    config.setMaximumPoolSize(10);

    ds = new HikariDataSource(config);
  }

  public static Connection getConnection() {
    try {
      Class.forName("org.postgresql.Driver");
      return ds.getConnection();
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public static HikariDataSource getDs() {
    return ds;
  }

  private HikariCPDataSource() {
  }
}
