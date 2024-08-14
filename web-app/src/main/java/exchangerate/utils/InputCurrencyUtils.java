package exchangerate.utils;

public class InputCurrencyUtils {

  public static boolean isCorrectCharCode(String charCode) {
    return charCode.matches("^[A-Z]{3}$");
  }
}
