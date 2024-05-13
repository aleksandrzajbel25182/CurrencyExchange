package com.currencyexchage.listner;

import com.currencyexchage.model.CurrencyCB;
import com.currencyexchage.repository.CurrenciesRepository;
import com.currencyexchage.repository.ExchangeRateRepository;
import com.currencyexchage.utils.ConnectionPool;
import com.currencyexchage.utils.JsonParser;
import com.currencyexchage.utils.UpdateDb;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;


@WebListener
public class ApplicationContextListner implements ServletContextListener {


  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ServletContext context = sce.getServletContext();
    DataSource dataSource = ConnectionPool.getDataSource();
    JsonParser jsonParser = new JsonParser();

    sce.getServletContext().setAttribute("dataSource", dataSource);

    CurrenciesRepository currenciesRepository = new CurrenciesRepository(dataSource);
    context.setAttribute("currenciesRepository", currenciesRepository);

    ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository(dataSource);
    context.setAttribute("exchangeRateRepository", exchangeRateRepository);

//    List<CurrencyCB> currencyCB = jsonParser.Parser();
//    currenciesRepository.create(currencyCB);
    UpdateDb updateDb = new UpdateDb(dataSource);
    updateDb.upd();

    System.out.println("contextInitialized listner");
    try {
      System.out.println(jsonParser.parserDate());
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
