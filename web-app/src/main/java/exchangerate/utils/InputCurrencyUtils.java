package exchangerate.utils;

public class InputCurrencyUtils {
  public static boolean isCorrectValid(String charCode){
    return charCode.matches("^[A-Z]{3}$");
  }
}
