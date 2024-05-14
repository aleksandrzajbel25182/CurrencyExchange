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
  private List<CurrencyCB> currencyCentralBankList;
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

    this.exchangeRateList = new ArrayList<>();
    this.currencyList = new ArrayList<>();

  }


  private boolean updateDate() {
    try {
      dateParser = jsParser.parserDate();
      currentDate = new CurrentDate(dateParser);

      if (!isMatch(dateParser)) {

        currentDateRepository.update(currentDate);
        System.out.println("The current date has been updated in the database");
//        if (currentDateRepository.isEmpty()) {
//          currentDateRepository.create(currentDate);
//          System.out.println("The current date is recorded in the database");
//        } else {

//        }
        return true;
      } else {
        System.out.println("The current date is relevant in the database");
        return false;
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }


  public void updateExchangeRate() {

    currencyCentralBankList = jsParser.parserCurrency();

    currencyList = currenciesRepository.get();

    var codeRUB = currenciesRepository.findByCode("RUB");

    if (updateDate()) {

      for (Currency currency : currencyList) {

        if (currency.getId() != codeRUB.getId()) {

          exchangeRateList.add(new ExchangeRate(
              currency.getId(),
              codeRUB,
              currency,
              new BigDecimal(
                  currencyCentralBankList.stream()
                      .filter(x ->
                          x.getCharCode().equals(currency.getCode()))
                      .findFirst()
                      .get()
                      .getValue()
              )
          ));
        }
      }
      exchangeRateRepository.updateAll(exchangeRateList);
      System.out.println("The exchange rate has been updated in the currency exchange table");
//      if (exchangeRateRepository.isEmpty()) {
//        exchangeRateRepository.createAll(exchangeRateList);
//        System.out.println("create exchangeRate");
//      } else {
//
//      }

    }

  }


  private boolean isMatch(Date hasDate) {
    return currentDateRepository.get(hasDate);
  }


}
