package com.currencyexchage.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {

  public static Connection getConnection() throws SQLException{

    Properties props = new Properties();
    try(InputStream in = Files.newInputStream(Paths.get("database.properties"))){
      props.load(in);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    String url = props.getProperty("DB_URL");
    String username = props.getProperty("DB_USER");
    String password = props.getProperty("DB_PASSWORD");

    return DriverManager.getConnection(url, username, password);
  }
}

