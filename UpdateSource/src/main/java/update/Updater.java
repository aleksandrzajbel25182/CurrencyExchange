package update;

import exchangerate.model.Currency;
import exchangerate.repository.CurrenciesRepository;
import exchangerate.repository.ExchangeRateRepository;
import exchangerate.model.ExchangeRate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import javax.sql.DataSource;
import update.Source.CurrencyExchangeRateSource;
import update.dto.ExchageRateDto;
import update.util.CurrencyController;
import update.util.ExchangeRateController;

public class Updater {

  private CurrencyExchangeRateSource source;

  private CurrenciesRepository currenciesRepository;

  private ExchangeRateRepository exchangeRateRepository;

  private LocalDate date;

  private ExchangeRateController exchangeRateController;
  private CurrencyController currencyController;

  public Updater(DataSource dataSource, CurrencyExchangeRateSource source, LocalDate date) {
    this.currencyController = new CurrencyController();
    this.exchangeRateController = new ExchangeRateController(dataSource);
    this.currenciesRepository = new CurrenciesRepository(dataSource);
    this.exchangeRateRepository = new ExchangeRateRepository(dataSource);
    this.source = source;
    this.date = date;
  }

  public void updateExchageRate() {

    List<ExchageRateDto> exchageRates = source.get(date);

    // 1. Generate a charCode map
    HashMap<String, ExchageRateDto> arrayCodeMap = getCharCodeMap(exchageRates);

    // 2. For this array, request currency identifiers from the database
    //    String -charCode Integer - id
    HashMap<String, Integer> idByCode = currenciesRepository.getCurrenciesIdByCode(
        arrayCodeMap.keySet());

    //To add currency pairs further, you need to add currencies to the database in the Currency table.
    // Therefore, put the charCode and the name of the currency in a separate Hashmap
    var charCodeAndNameMap = new HashMap<String, String>();
    for (Map.Entry<String, ExchageRateDto> entry : arrayCodeMap.entrySet()) {
      if (!idByCode.containsKey(entry)) {
        charCodeAndNameMap.put(entry.getKey(), entry.getValue().getName());
      }
    }
    if (charCodeAndNameMap.isEmpty()) {
      var currenciesToInsertList = currencyController.toEntity(charCodeAndNameMap);
      insertCurrencies(currenciesToInsertList);
    }

    var exchangeRatesToUpdate = new ArrayList<Pair<Integer, ExchageRateDto>>();
    var exchangeRatesToInsert = new ArrayList<ExchageRateDto>();

    for (Map.Entry<String, ExchageRateDto> entry : arrayCodeMap.entrySet()) {
      ExchageRateDto exchageRateDto = entry.getValue();
      if (idByCode.containsKey(exchageRateDto.getCharCode())) {
        exchangeRatesToUpdate.add(
            new Pair<>(idByCode.get(exchageRateDto.getCharCode()), exchageRateDto));
      } else {
        exchangeRatesToInsert.add(exchageRateDto);
      }
    }

    // Converting Dto to Entity
    List<ExchangeRate> exchangeToUpdate = exchangeRateController.toEntity(exchangeRatesToUpdate);
    for (ExchangeRate entry : exchangeToUpdate) {
      exchangeRateRepository.update(entry);
    }

//    System.out.println("Update complete");
//    System.out.println("The exchange rate has been updated in the database successfully");

    // Make a check for an empty list
    if (exchangeRatesToInsert != null) {
      List<ExchangeRate> exchangeInsert = exchangeRateController.toEntity(exchangeRatesToInsert);
      for (ExchangeRate entry : exchangeInsert) {
        exchangeRateRepository.create(entry);
      }
    }

//    System.out.println("The addition is completed");
//    System.out.println("The new exchange rate has been added to the database successfully");

  }

  private HashMap<String, ExchageRateDto> getCharCodeMap(List<ExchageRateDto> exchageRates) {
    HashMap<String, ExchageRateDto> arrayCodeMap = new HashMap<>();
    for (ExchageRateDto currency : exchageRates) {
      arrayCodeMap.put(currency.getCharCode(), currency);
    }
    return arrayCodeMap;
  }

  private void insertCurrencies(List<Currency> currenciesToUpdate) {
    if (currenciesToUpdate != null) {
      for (Currency currency : currenciesToUpdate) {
        currenciesRepository.create(currency);
      }
    }
//    System.out.println("New currencies have been successfully added");
  }
}

