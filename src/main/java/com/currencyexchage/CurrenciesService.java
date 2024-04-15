package com.currencyexchage;

import com.currencyexchage.model.Currencies;
import java.util.List;

public class CurrenciesService {

  public List<Currencies> getCurrencies() {

    var сurrency = List.of(
        new Currencies(1, "RUB","Russian ruble","₽"),
        new Currencies(2, "EUR","Euro","€"),
        new Currencies(3, "AUD","Australian dollar","$"),
        new Currencies(4, "Br","Belarusian ruble","Br"),
        new Currencies(5, "AMD","Armenian dram","֏")
    );


    return сurrency;
  }
}
