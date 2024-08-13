package exchangerate.servlets;

import exchangerate.validation.Validation;
import exchangerate.error.ErrorMessage;

import static exchangerate.error.ErrorHandler.sendError;

import com.entities.ExchangeRate;
import com.repository.ExchangeRateRepository;
import com.util.JsonConvert;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@WebServlet("/exchangeRates/*")
public class ExchangeRateServlet extends HttpServlet {

  private ExchangeRateRepository exchangeRateRepository;

  @Override
  public void init(ServletConfig config) throws ServletException {
    exchangeRateRepository = (ExchangeRateRepository) config.getServletContext()
        .getAttribute("exchangeRateRepository");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PrintWriter writer = resp.getWriter();

    String currenciesCodes = req.getPathInfo().replaceFirst("/", "").toUpperCase();
    if (!Validation.validateExchangeRate(currenciesCodes, resp)) {
      return;
    }

    var baseCurrency = currenciesCodes.substring(0, 3);
    var targetCurrency = currenciesCodes.substring(3, 6);
    Optional<ExchangeRate> exchangeRate = exchangeRateRepository.finByCode(baseCurrency,
        targetCurrency);
    if (exchangeRate.isPresent()) {
      String message = JsonConvert.jsonConvert(exchangeRate.get());
      writer.write(message);
    } else {
      sendError(ErrorMessage.EXCHANGER_RATE_NOT_FOUND, resp);
    }
  }

}



