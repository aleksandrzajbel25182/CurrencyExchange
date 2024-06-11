package exchangerate.servlets;

import exchangerate.repository.ExchangeRateRepository;
import exchangerate.utils.JsonConvert;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
    var message = JsonConvert.jsonConvert(exchangeRateRepository.finByCode(baseCurrency, targetCurrency));
    writer.write(message);
  }


}
