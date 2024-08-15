package exchangerate.servlets;

import static exchangerate.error.ErrorHandler.sendError;

import exchangerate.error.ErrorMessage;
import exchangerate.validation.Validation;

import com.entities.Subscriptions;
import com.repository.ExchangeRateRepository;
import com.repository.SubscriptionsRepository;
import com.util.JsonConvert;

import jakarta.servlet.ServletConfig;

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

  @Override
  public void init(ServletConfig config) {
    subscriptionsRepository = (SubscriptionsRepository) config.getServletContext()
        .getAttribute("subscriptionsRepository");
    exchangeRateRepository = (ExchangeRateRepository) config.getServletContext()
        .getAttribute("exchangeRateRepository");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    Subscriptions subscriptions = new Subscriptions();
    PrintWriter writer = resp.getWriter();
    String url = req.getParameter("url");
    String baseCurrency = req.getParameter("base").toUpperCase();
    String targetCurrency = req.getParameter("target").toUpperCase();

    if (!Validation.validateEmptyAndCorrectString(url, baseCurrency, targetCurrency, resp)) {
      return;
    }

    var exchangeRate = exchangeRateRepository.finByCode(baseCurrency, targetCurrency);
    if (exchangeRate.isPresent()) {
      subscriptions.setUrl(url);
      subscriptions.setBaseCurrencyId(exchangeRate.get().getBaseCurrencyId());
      subscriptions.setTargetCurrencyId(exchangeRate.get().getTargetCurrencyId());
      subscriptions.setRate(exchangeRate.get().getRate());
      subscriptions.setDate(exchangeRate.get().getDate());
      subscriptions.setStatus("not sent");

      subscriptionsRepository.upsert(subscriptions);

      var message = JsonConvert.jsonConvert(subscriptions);
      writer.write(message);
    } else {
      sendError(ErrorMessage.EXCHANGER_RATE_NOT_FOUND, resp);
    }
  }
}
