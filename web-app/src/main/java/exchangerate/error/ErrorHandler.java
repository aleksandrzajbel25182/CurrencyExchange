package exchangerate.error;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorHandler {

  public static void sendError(ErrorMessage error, HttpServletResponse response)
      throws IOException {
    response.sendError(error.getStatus(),error.getMessage());
  }

}
