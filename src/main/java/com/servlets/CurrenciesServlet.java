package com.servlets;

import com.currencyexchage.repository.CurrenciesRepository;
import com.currencyexchage.utils.JsonConvert;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

  private CurrenciesRepository currencyRepository;

  @Override
  public void init(ServletConfig config) {
    currencyRepository = (CurrenciesRepository) config.getServletContext()
        .getAttribute("currenciesRepository");
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    PrintWriter writer = response.getWriter();

    var message = JsonConvert.jsonConvert(currencyRepository.get());
    writer.write(message);

  }

}
