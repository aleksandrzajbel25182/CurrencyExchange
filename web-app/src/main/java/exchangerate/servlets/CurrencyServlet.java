package exchangerate.servlets;

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

    int id = Integer.parseInt(req.getParameter("id"));
    var message = JsonConvert.jsonConvert(currencyRepository.findById(id));
    writer.write(message);
  }


}
