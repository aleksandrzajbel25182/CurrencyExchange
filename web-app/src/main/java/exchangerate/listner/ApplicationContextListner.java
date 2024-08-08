package exchangerate.listner;


import com.repository.CurrenciesRepository;
import com.repository.ExchangeRateRepository;
import com.repository.SubscriptionsRepository;
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

    SubscriptionsRepository subscriptionsRepository = new SubscriptionsRepository(dataSource);
    context.setAttribute("subscriptionsRepository", subscriptionsRepository);

    System.out.println("ContextInitialized listner");

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
