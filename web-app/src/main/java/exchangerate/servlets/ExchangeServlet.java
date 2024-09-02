package exchangerate.servlets;

import static exchangerate.error.ErrorHandler.sendError;

import exchangerate.error.ErrorMessage;
import exchangerate.service.ExchangeService;
import exchangerate.validation.Validation;

import com.entities.ExchangeDTO;
import com.repository.CurrenciesRepository;
import com.repository.ExchangeRateRepository;
import com.util.JsonConvert;

import jakarta.servlet.ServletConfig;
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
      throws IOException {

    String from = req.getParameter("from").toUpperCase();
    String to = req.getParameter("to").toUpperCase();
    String stringAmount = req.getParameter(req.getParameter("amount"));
    if (!Validation.validateEmptyAndCorrectString(stringAmount, from, to, resp)) {
      return;
    }

    BigDecimal amount = new BigDecimal(stringAmount);
    BigDecimal rate = exchangeService.getRate(from, to);

    if (rate == null) {
      sendError(ErrorMessage.EXCHANGER_RATE_NOT_FOUND, resp);
      return;
    }
    ExchangeDTO exchangeDTO = new ExchangeDTO(
        currencyRepository.findByCode(from).get(),
        currencyRepository.findByCode(to).get(),
        rate,
        amount,
        rate.multiply(amount)
    );

    PrintWriter writer = resp.getWriter();
    var message = JsonConvert.jsonConvert(exchangeDTO);
    writer.write(message);
  }
}
