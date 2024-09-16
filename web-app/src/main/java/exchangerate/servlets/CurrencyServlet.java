package exchangerate.servlets;


import exchangerate.error.ErrorMessage;
import exchangerate.validation.Validation;

import static exchangerate.error.ErrorHandler.sendError;

import com.entities.Currency;
import com.repository.CurrenciesRepository;
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


@WebServlet("/currency")
public class CurrencyServlet extends HttpServlet {

  private CurrenciesRepository currencyRepository;

  @Override
  public void init(ServletConfig config) {
    currencyRepository = (CurrenciesRepository) config.getServletContext()
        .getAttribute("currenciesRepository");

  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    PrintWriter writer = resp.getWriter();
    String code = req.getParameter("code").toUpperCase();

    if (!Validation.validateCharCode(code, resp)) {
      return;
    }

    Optional<Currency> currency = currencyRepository.findByCode(code);
    if (currency.isPresent()) {
      String message = JsonConvert.jsonConvert(currency.get());
      writer.write(message);
    } else {
      sendError(ErrorMessage.CURRENCY_NOT_FOUND, resp);
    }
  }

}
