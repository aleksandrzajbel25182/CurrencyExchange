package exchangerate.error;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ErrorHandler {

  void sendError(ErrorMessage error, HttpServletResponse response) throws IOException;

}
