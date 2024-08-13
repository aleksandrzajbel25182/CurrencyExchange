package exchangerate.servlets;

import com.entities.Subscriptions;
import com.repository.ExchangeRateRepository;
import com.repository.SubscriptionsRepository;
import com.util.JsonConvert;
import exchangerate.error.ErrorHandler;
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

  private ExchangeRateRepository exchangeRateRepository;

  private ErrorHandler errorHandler;

  @Override
  public void init(ServletConfig config) throws ServletException {
    subscriptionsRepository = (SubscriptionsRepository) config.getServletContext()
        .getAttribute("subscriptionsRepository");
    exchangeRateRepository = (ExchangeRateRepository) config.getServletContext()
        .getAttribute("exchangeRateRepository");
    errorHandler = (ErrorHandler) config.getServletContext().getAttribute("errorHandler");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    Subscriptions subscriptions = new Subscriptions();
    PrintWriter writer = resp.getWriter();
    String url = req.getParameter("url");
    String baseCurrency = req.getParameter("base");
    String targetCurrency = req.getParameter("target");

    var exchangeRate = exchangeRateRepository.finByCode(baseCurrency, targetCurrency);
    if (exchangeRate.isPresent()) {
      subscriptions.setUrl(url);
      subscriptions.setBaseCurrencyId(exchangeRate.get().getBaseCurrencyId());
      subscriptions.setTargetCurrencyId(exchangeRate.get().getTargetCurrencyId());
      subscriptions.setRate(exchangeRate.get().getRate());
      subscriptions.setDate(exchangeRate.get().getDate());
      subscriptions.setStatus("не отправлено");

      subscriptionsRepository.upsert(subscriptions);

      var message = JsonConvert.jsonConvert(subscriptions);
      writer.write(message);
    } else {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND, "There is no exchange rate");
    }
  }
}
