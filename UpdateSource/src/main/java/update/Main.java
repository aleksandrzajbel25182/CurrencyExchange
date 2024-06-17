package update;

import java.util.HashMap;

public class Main {

  public static void main(String[] args) {

    HashMap<String, String> map = new HashMap<String, String>();
    map.put("x", "y");

    String value = map.get("y"); // value = "y"
    System.out.println(value);


  }

}