package com.currencyexchage.utils;

import com.currencyexchage.model.Currency;
import com.currencyexchage.model.CurrencyCB;
import com.currencyexchage.model.CurrentDate;
import com.currencyexchage.model.ExchangeRate;
import com.currencyexchage.repository.CurrenciesRepository;
import com.currencyexchage.repository.CurrentDateRepository;
import com.currencyexchage.repository.ExchangeRateRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;

public class UpdateDb {

  private List<ExchangeRate> exchangeRateList;
  private List<Currency> currencyList;
  private ExchangeRateRepository exchangeRateRepository;
  private CurrenciesRepository currenciesRepository;
  private CurrentDateRepository currentDateRepository;
  private CurrentDate currentDate;

  private Date dateParser;
  private DataSource dataSource;

  private JsonParser jsParser;

  public UpdateDb(DataSource dataSource) {
    this.jsParser = new JsonParser();
    this.dataSource = dataSource;

    this.exchangeRateRepository = new ExchangeRateRepository(this.dataSource);
    this.currenciesRepository = new CurrenciesRepository(this.dataSource);

    this.currentDateRepository = new CurrentDateRepository(this.dataSource);
    exchangeRateList = new ArrayList<>();
    currencyList = new ArrayList<>();

  }


  private boolean updateDate() {

    try {
      dateParser = jsParser.parserDate();
      currentDate = new CurrentDate(dateParser);

      if (isMatch(dateParser) == false) {
        if (currentDateRepository.isEmpty()) {
          currentDateRepository.create(currentDate);
          System.out.println("currentDate create");
        } else {
          currentDateRepository.update(currentDate);
          System.out.println("currentDate update");
        }
        return true;
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  public void upd() {
//    exchangeRateList = exchangeRateRepository.get();
    List<CurrencyCB> currencyCB_List = jsParser.parserCurrency();
    currencyList = currenciesRepository.get();
    var codeRUB = currenciesRepository.findByCode("RUB");
//    if (updateDate()) {

    for (Currency currency : currencyList) {
      if (currency.getId() != codeRUB.getId()) {
        exchangeRateList.add(new ExchangeRate(
            currency.getId(),
            codeRUB,
            currency,
            new BigDecimal(
                currencyCB_List.stream()
                    .filter(x ->
                        x.getCharCode() == currency.getCode())
                    .findFirst()
                    .get()
                    .getValue()
            )
        ));
      }
    }
    if (exchangeRateRepository.isEmpty() == false) {
      exchangeRateRepository.createAll(exchangeRateList);
      System.out.println("create exchangeRate");
    } else {

    }

//    }

  }


  private int findByRate(String hasCharCode) {

    return 0;
  }


  private boolean isMatch(Date hasDate) {
    return currentDateRepository.get(hasDate);
  }


}
