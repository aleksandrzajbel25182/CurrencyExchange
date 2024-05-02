package com.currencyexchage.listner;

import com.currencyexchage.repository.CurrenciesRepository;
import com.currencyexchage.repository.ExchangeRateRepository;
import com.currencyexchage.utils.ConnectionPool;
import com.currencyexchage.utils.JsonParser;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;


@WebListener
public class ApplicationContextListner implements ServletContextListener {


  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ServletContext context = sce.getServletContext();
    DataSource dataSource = ConnectionPool.getDataSource();

    sce.getServletContext().setAttribute("dataSource", dataSource);

    CurrenciesRepository currenciesRepository = new CurrenciesRepository(dataSource);
    context.setAttribute("currenciesRepository", currenciesRepository);

    ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository(dataSource);
    context.setAttribute("exchangeRateRepository", exchangeRateRepository);

    System.out.println("contextInitialized listner");

    JsonParser js = new JsonParser();
    try {
      js.Parser();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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
