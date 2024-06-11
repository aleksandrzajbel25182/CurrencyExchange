package exchangerate.listner;

import exchangerate.repository.CurrenciesRepository;
import exchangerate.repository.ExchangeRateRepository;
import exchangerate.utils.ConnectionPool;
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
    DataSource dataSource = ConnectionPool.getDataSource();
//    JsonParser jsonParser = new JsonParser();

    sce.getServletContext().setAttribute("dataSource", dataSource);

    CurrenciesRepository currenciesRepository = new CurrenciesRepository(dataSource);
    context.setAttribute("currenciesRepository", currenciesRepository);

    ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository(dataSource);
    context.setAttribute("exchangeRateRepository", exchangeRateRepository);

//    UpdateDb updateDb = new UpdateDb(dataSource);
//    updateDb.updateExchangeRate();

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
