package exchangerate.error;

import jakarta.servlet.http.HttpServletResponse;

public enum ErrorMessage {

  CURRENCY_NOT_FOUND("Currency not found",
      HttpServletResponse.SC_NOT_FOUND),

  EXCHANGER_RATE_NOT_FOUND("The exchange rate was not found",
      HttpServletResponse.SC_NOT_FOUND),
  CODE_NOT_IN_ADDRESS("Currency code is not in address",
      HttpServletResponse.SC_BAD_REQUEST),
  EMPTY_FORM_FIELD("Required form field is missing",
      HttpServletResponse.SC_BAD_REQUEST),
  DATA_IS_INVALID("Input data is invalid", HttpServletResponse.SC_BAD_REQUEST),
  ERROR("Application error",
      HttpServletResponse.SC_INTERNAL_SERVER_ERROR);


  private final String message;
  private final int status;

  ErrorMessage(String message, int status) {
    this.message = message;
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public int getStatus() {
    return status;
  }
}
