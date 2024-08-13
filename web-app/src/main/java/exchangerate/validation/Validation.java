package exchangerate.validation;

import static exchangerate.utils.InputCurrencyUtils.isCorrectValid;
import static exchangerate.utils.InputStringUtils.isEmptyField;

import exchangerate.error.ErrorHandler;
import exchangerate.error.ErrorMessage;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Validation {


  public static boolean validateCharCode(String charCode,
      HttpServletResponse response) throws IOException {
    if (isEmptyField(charCode)) {
      ErrorHandler.sendError(ErrorMessage.EMPTY_FORM_FIELD,
          response);
      return false;
    }
    if (!isCorrectValid(charCode)) {
      ErrorHandler.sendError(ErrorMessage.DATA_IS_INVALID, response);
      return false;
    }
    return true;
  }

  public static boolean validateExchangeRate(String pathInfo,
      HttpServletResponse response) throws IOException {
    if(isEmptyField(pathInfo)){
      ErrorHandler.sendError(ErrorMessage.EMPTY_FORM_FIELD,
          response);
      return false;
    }
    var baseCharCode = pathInfo.substring(0, 3);
    var targetCharCode = pathInfo.substring(3, 6);
    if (!validateCharCode(baseCharCode, response) || !validateCharCode(targetCharCode,
        response)) {
      return false;
    }
    return true;
  }
}
