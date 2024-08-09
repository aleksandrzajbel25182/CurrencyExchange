package exchangerate.servlets;

import com.entities.Subscriptions;
import com.repository.CurrenciesRepository;
import com.repository.ExchangeRateRepository;
import com.repository.SubscriptionsRepository;
import com.util.JsonConvert;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(("/exchangeRates/subscription"))
public class ExchangeRateSubscriptionServlet extends HttpServlet {

  private SubscriptionsRepository subscriptionsRepository;
  private CurrenciesRepository currencyRepository;
  private ExchangeRateRepository exchangeRateRepository;

  @Override
  public void init(ServletConfig config) throws ServletException {
    subscriptionsRepository = (SubscriptionsRepository) config.getServletContext()
        .getAttribute("subscriptionsRepository");
    currencyRepository = (CurrenciesRepository) config.getServletContext()
        .getAttribute("currenciesRepository");
    exchangeRateRepository = (ExchangeRateRepository) config.getServletContext()
        .getAttribute("exchangeRateRepository");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    Subscriptions subscriptions = new Subscriptions();
    String url = req.getParameter("url");
    String baseCurrency = req.getParameter("base");
    String targetCurrency = req.getParameter("target");

    var exchangeRate = exchangeRateRepository.finByCode(baseCurrency, targetCurrency);
    if (exchangeRate != null) {
      subscriptions.setUrl(url);
      subscriptions.setBaseCurrencyId(exchangeRate.getBaseCurrencyId());
      subscriptions.setTargetCurrencyId(exchangeRate.getTargetCurrencyId());
      subscriptions.setRate(exchangeRate.getRate());
      subscriptions.setDate(exchangeRate.getDate());
      subscriptions.setStatus("не отправлено");

      subscriptionsRepository.upsert(subscriptions);

      PrintWriter writer = resp.getWriter();
      var message = JsonConvert.jsonConvert(subscriptions);
      writer.write(message);
    }

  }
}
