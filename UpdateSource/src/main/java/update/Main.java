package update;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import update.Source.CBRFSource;

public class Main {

  public static void main(String[] args) {
    System.out.println("Hello world!");
    CBRFSource cbrf = new CBRFSource();
    List<CurrencyDTO> list = cbrf.get(LocalDate.now());

    for (CurrencyDTO cur : list)
    {
      System.out.println(cur.getName() + " "+  cur.getCharCode());

    }

  }
}