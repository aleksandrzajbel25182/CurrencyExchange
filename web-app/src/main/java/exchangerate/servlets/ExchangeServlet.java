package exchangerate.servlets;

import com.entities.Exchange;
import com.repository.CurrenciesRepository;
import com.repository.ExchangeRateRepository;
import exchangerate.service.ExchangeService;
import com.util.JsonConvert;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

  private ExchangeRateRepository exchangeRateRepository;

  private CurrenciesRepository currencyRepository;

  private ExchangeService exchangeService;
  @Override
  public void init(ServletConfig config) {
    exchangeRateRepository = (ExchangeRateRepository) config.getServletContext()
        .getAttribute("exchangeRateRepository");
    currencyRepository = (CurrenciesRepository) config.getServletContext()
        .getAttribute("currenciesRepository");

    exchangeService = new ExchangeService(exchangeRateRepository);
  }


  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    String from = req.getParameter("from");
    String to = req.getParameter("to");
    BigDecimal amount = new BigDecimal(req.getParameter("amount"));
    BigDecimal rate = exchangeService.getRate(from, to);

    if (rate == null) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Не существует курс обмена");
      return;
    }
    Exchange exchange = new Exchange(
        currencyRepository.findByCode(from).get(),
        currencyRepository.findByCode(to).get(),
        rate,
        amount,
        rate.multiply(amount)
    );

    PrintWriter writer = resp.getWriter();
    var message = JsonConvert.jsonConvert(exchange);
    writer.write(message);

  }
}
