package com.servlets;

import com.currencyexchage.repository.ExchangeRateRepository;
import com.currencyexchage.utils.JsonConvert;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

  private ExchangeRateRepository exchangeRateRepository;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    PrintWriter writer = resp.getWriter();
    var message = JsonConvert.jsonConvert(exchangeRateRepository.get());
    writer.write(message);
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    exchangeRateRepository = (ExchangeRateRepository) config.getServletContext()
        .getAttribute("exchangeRateRepository");
  }
}
