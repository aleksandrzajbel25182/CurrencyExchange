package exchangerate.utils;

public class InputExchangeRateUtils {

  public static boolean isCorrectPair(String pair) {
    return pair.length() == 6;
  }
}
