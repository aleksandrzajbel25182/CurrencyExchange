package exchangerate.validation;

import exchangerate.error.ErrorHandler;
import exchangerate.error.ErrorMessage;
import exchangerate.utils.InputExchangeRateUtils;
import exchangerate.utils.InputCurrencyUtils;
import exchangerate.utils.InputStringUtils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class Validation {


  public static boolean validateCharCode(String charCode,
      HttpServletResponse response) throws IOException {
    if (InputStringUtils.isEmptyField(charCode)) {
      ErrorHandler.sendError(ErrorMessage.EMPTY_FORM_FIELD,
          response);
      return false;
    }
    if (!InputCurrencyUtils.isCorrectCharCode(charCode)) {
      ErrorHandler.sendError(ErrorMessage.DATA_IS_INVALID, response);
      return false;
    }
    return true;
  }

  public static boolean validateExchangeRate(String pathInfo,
      HttpServletResponse response) throws IOException {
    if (InputStringUtils.isEmptyField(pathInfo)) {
      ErrorHandler.sendError(ErrorMessage.EMPTY_FORM_FIELD,
          response);
      return false;
    }
    if (!InputExchangeRateUtils.isCorrectPair(pathInfo)) {
      ErrorHandler.sendError(ErrorMessage.DATA_IS_INVALID, response);
      return false;
    }
    return true;
  }

  public static boolean validateExchangeRate(String baseCharCode, String targetCharCode,
      HttpServletResponse response) throws IOException {
    if (InputStringUtils.isEmptyField(baseCharCode, targetCharCode)) {
      ErrorHandler.sendError(ErrorMessage.EMPTY_FORM_FIELD,
          response);
      return false;
    }
    if (!InputCurrencyUtils.isCorrectCharCode(baseCharCode)
        || !InputCurrencyUtils.isCorrectCharCode(targetCharCode)) {
      ErrorHandler.sendError(ErrorMessage.DATA_IS_INVALID, response);
      return false;
    }
    return true;
  }

  public static boolean validateEmptyAndCorrectString(String s1, String baseCharCode,
      String targetCharCode,
      HttpServletResponse response) throws IOException {
    if (InputStringUtils.isEmptyField(s1, baseCharCode, targetCharCode)) {
      ErrorHandler.sendError(ErrorMessage.EMPTY_FORM_FIELD,
          response);
      return false;
    }
    if (!InputCurrencyUtils.isCorrectCharCode(baseCharCode)
        || !InputCurrencyUtils.isCorrectCharCode(targetCharCode)) {
      ErrorHandler.sendError(ErrorMessage.DATA_IS_INVALID, response);
      return false;
    }
    return true;
  }
}
