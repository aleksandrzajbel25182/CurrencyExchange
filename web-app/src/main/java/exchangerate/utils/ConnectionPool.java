package exchangerate.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

  private static HikariConfig config = new HikariConfig();
  private static final HikariDataSource dataSource;

  static {
    try {
      Class.forName(System.getenv("DATABASE_DRIVER"));
      config.setJdbcUrl(System.getenv("DATABASE_URL"));
      config.setUsername(System.getenv("DATABASE_USER"));
      config.setPassword(System.getenv("DATABASE_PASSWORD"));
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
