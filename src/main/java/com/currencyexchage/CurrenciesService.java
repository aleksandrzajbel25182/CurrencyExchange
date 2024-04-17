package com.currencyexchage;

import com.currencyexchage.model.Currency;
import java.util.List;

public class CurrenciesService {

  public List<Currency> getCurrencies() {

    var сurrency = List.of(
        new Currency(1, "RUB","Russian ruble","₽"),
        new Currency(2, "EUR","Euro","€"),
        new Currency(3, "AUD","Australian dollar","$"),
        new Currency(4, "Br","Belarusian ruble","Br"),
        new Currency(5, "AMD","Armenian dram","֏")
    );


    return сurrency;
  }
}
