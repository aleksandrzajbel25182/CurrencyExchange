package com.currencyexchage.listner;

import com.currencyexchage.repository.CurrenciesRepository;
import com.currencyexchage.utils.ConnectionPool;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;


@WebListener
public class ApplicationContextListner implements ServletContextListener {


  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ServletContext context = sce.getServletContext();
//
//    Properties props = new Properties();
//
//    try (InputStream in = DatabaseConfig.class
//        .getClassLoader()
//        .getResourceAsStream("database.properties")) {
//      props.load(in);
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//
//    ConnectionPool connectionPool = null;
//    try {
//      connectionPool = BasicConnectionPool.create(
//          props.getProperty("DB_URL"),
//          props.getProperty("DB_USER"),
//          props.getProperty("DB_PASSWORD")
//      );
//    } catch (SQLException e) {
//      throw new RuntimeException(e);
//    }
//
//    context.setAttribute("dbConnection", connectionPool);

    DataSource dataSource = ConnectionPool.getDataSource();

    sce.getServletContext().setAttribute("dataSource", dataSource);

    CurrenciesRepository currenciesRepository = new CurrenciesRepository(dataSource);
    context.setAttribute("currenciesRepository", currenciesRepository);

    System.out.println("contextInitialized listner");

  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    try {
      ((Connection) sce.getServletContext().getAttribute("dataSource")).close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


}
