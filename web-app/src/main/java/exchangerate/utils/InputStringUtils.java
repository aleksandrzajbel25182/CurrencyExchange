package exchangerate.utils;

public class InputStringUtils {

  public static boolean isEmptyField(String s1) {
    return s1.isEmpty();
  }

  public static boolean isEmptyField(String s1, String s2) {
    return s1.isEmpty() || s2.isEmpty();
  }

  public static boolean isEmptyField(String s1, String s2, String s3) {
    return s1.isEmpty() || s2.isEmpty() || s3.isEmpty();
  }

}
