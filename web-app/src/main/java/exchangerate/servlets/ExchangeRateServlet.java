package exchangerate.servlets;

import com.entities.ExchangeRate;
import com.repository.ExchangeRateRepository;
import com.util.JsonConvert;
import exchangerate.error.DefaultErrorHandler;
import exchangerate.error.ErrorHandler;
import exchangerate.error.ErrorMessage;
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

  private ErrorHandler errorHandler;

  @Override
  public void init(ServletConfig config) throws ServletException {
    exchangeRateRepository = (ExchangeRateRepository) config.getServletContext()
        .getAttribute("exchangeRateRepository");
    errorHandler = (ErrorHandler) config.getServletContext().getAttribute("errorHandler");

  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String currenciesCodes = req.getPathInfo().replaceFirst("/", "").toUpperCase();

    var baseCurrency = currenciesCodes.substring(0, 3);
    var targetCurrency = currenciesCodes.substring(3, 6);
    PrintWriter writer = resp.getWriter();

    Optional<ExchangeRate> exchangeRate = exchangeRateRepository.finByCode(baseCurrency,
        targetCurrency);
    if (exchangeRate.isPresent()) {
      String message = JsonConvert.jsonConvert(exchangeRate.get());
      writer.write(message);
    } else {
      errorHandler.sendError(ErrorMessage.EXCHANGER_RATE_NOT_FOUND, resp);
    }

  }


}
