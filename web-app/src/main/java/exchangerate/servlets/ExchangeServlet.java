package exchangerate.servlets;

import com.fasterxml.jackson.databind.node.TextNode;
import exchangerate.model.Exchange;
import exchangerate.model.ExchangeRate;
import exchangerate.repository.CurrenciesRepository;
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
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

  private ExchangeRateRepository exchangeRateRepository;

  private CurrenciesRepository currencyRepository;

  @Override
  public void init(ServletConfig config) {
    exchangeRateRepository = (ExchangeRateRepository) config.getServletContext()
        .getAttribute("exchangeRateRepository");
    currencyRepository = (CurrenciesRepository) config.getServletContext()
        .getAttribute("currenciesRepository");
  }


  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    String from = req.getParameter("from");
    String to = req.getParameter("to");
    BigDecimal amount =  new BigDecimal(req.getParameter("amount"));
    BigDecimal rate = getRate(from,to);

    if(rate == null){
      resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Не существует курс обмена");
      return;
    }
    Exchange exchange = new Exchange(
        currencyRepository.findByCode(from),
        currencyRepository.findByCode(to),
        rate,
        amount,
        rate.multiply(amount)
    );

    PrintWriter writer = resp.getWriter();
    var message = JsonConvert.jsonConvert(exchange);
    writer.write(message);

  }

  private BigDecimal getRate(String from, String to) {

    // 1. There is a currency pair AB in the `ExchangeRates` table - we take its rate
    ExchangeRate exchangeRate = exchangeRateRepository.finByCode(from, to);
    if (exchangeRate != null) {
      return exchangeRate.getRate();
    }
    // 2. In the `ExchangeRates` table, there is a currency pair BA - take its rate,
    //    and count the reverse to get AB
    ExchangeRate reverseExchangeRate = exchangeRateRepository.finByCode(to, from);
    if (reverseExchangeRate != null) {
      return new BigDecimal(1).divide(reverseExchangeRate.getRate());
    }

    // 3. In the `ExchangeRates` table, there are currency pairs RUB-A and RUB-B
    //    calculate the AB rate from these rates
    ExchangeRate exchangeRateRubToA = exchangeRateRepository.finByCode("RUB", from);
    ExchangeRate exchangeRateRubToB = exchangeRateRepository.finByCode("RUB", to);

    if(exchangeRateRubToA!=null && exchangeRateRubToB!=null){
      return exchangeRateRubToA.getRate().divide(exchangeRateRubToB.getRate());
    }

    return null;
  }
}
