package com.servlets;

import com.currencyexchage.CurrenciesService;
import com.currencyexchage.utils.JsonConvert;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/currencies")
public class TestServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    PrintWriter writer = response.getWriter();

    CurrenciesService currenciesService = new CurrenciesService();
    var message  = JsonConvert.jsonConvert(currenciesService.getCurrencies());
    writer.write(message);


  }
}
