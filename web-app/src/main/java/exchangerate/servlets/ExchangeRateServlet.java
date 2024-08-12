package exchangerate.servlets;

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
      resp.sendError(HttpServletResponse.SC_NOT_FOUND,
          "There is no such exchange rate in the database");

    }

  }


}
