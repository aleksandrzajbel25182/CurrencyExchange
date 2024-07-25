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
import update.dto.ExchangeRateDto;
import update.util.CurrencyConverterToEntity;
import update.util.ExchangeRateConverterToEntity;

public class Updater {

  private CurrencyExchangeRateSource source;

  private CurrenciesRepository currenciesRepository;

  private ExchangeRateRepository exchangeRateRepository;

  private LocalDate date;

  private ExchangeRateConverterToEntity exchangeRateConverterToEntity;
  private CurrencyConverterToEntity currencyConverterToEntity;

  public Updater(DataSource dataSource, CurrencyExchangeRateSource source, LocalDate date) {
    this.currencyConverterToEntity = new CurrencyConverterToEntity();
    this.exchangeRateConverterToEntity = new ExchangeRateConverterToEntity(dataSource);
    this.currenciesRepository = new CurrenciesRepository(dataSource);
    this.exchangeRateRepository = new ExchangeRateRepository(dataSource);
    this.source = source;
    this.date = date;
  }

  public void updateExchageRate() {

    List<ExchangeRateDto> exchageRates = source.get(date);

    // 1. Generate a charCode map
    HashMap<String, ExchangeRateDto> arrayCodeMap = getCharCodeMap(exchageRates);

    // 2. For this array, request currency identifiers from the database
    //    String -charCode Integer - id
    HashMap<String, Integer> idByCode = currenciesRepository.getCurrenciesIdByCode(
        arrayCodeMap.keySet());

    // 2.1 To add currency pairs further, you need to add currencies to the database in the Currency table.
    //     Therefore, put the charCode and the name of the currency in a separate Hashmap
    var charCodeAndNameMap = new HashMap<String, String>();
    for (Map.Entry<String, ExchangeRateDto> entry : arrayCodeMap.entrySet()) {
      if (!idByCode.containsKey(entry)) {
        charCodeAndNameMap.put(entry.getKey(), entry.getValue().getName());
      }
    }
    if (charCodeAndNameMap.isEmpty()) {
      var currenciesToInsertList = currencyConverterToEntity.toEntity(charCodeAndNameMap);
      currenciesRepository.createBatch(currenciesToInsertList);
    }

    // 3. Extract the courses from the database according to the received ids for the date specified

    var exchangeRatesToUpdate = new ArrayList<Pair<Integer, ExchangeRateDto>>();
    var exchangeRatesToInsert = new ArrayList<ExchangeRateDto>();

    for (Map.Entry<String, Integer> entry : idByCode.entrySet()) {
      var exchangeRateId = exchangeRateRepository.getByIdExchangeRate((entry.getValue()));

      if (exchangeRateId != null) {
        exchangeRatesToUpdate.add(new Pair<>(exchangeRateId, arrayCodeMap.get(entry.getKey())));
      } else {
        exchangeRatesToInsert.add(arrayCodeMap.get(entry.getKey()));
      }
    }

    // Converting Dto to Entity
    List<ExchangeRate> exchangeToUpdate = exchangeRateConverterToEntity.toEntity(
        exchangeRatesToUpdate);
    for (ExchangeRate entry : exchangeToUpdate) {
      exchangeRateRepository.update(entry);
    }

    // Make a check for an empty list
    if (exchangeRatesToInsert != null) {
      List<ExchangeRate> exchangeInsert = exchangeRateConverterToEntity.toEntity(
          exchangeRatesToInsert);
      exchangeRateRepository.createBatch(exchangeInsert);

    }
  }

  private HashMap<String, ExchangeRateDto> getCharCodeMap(List<ExchangeRateDto> exchageRates) {
    HashMap<String, ExchangeRateDto> arrayCodeMap = new HashMap<>();
    for (ExchangeRateDto exchangeRate : exchageRates) {
      arrayCodeMap.put(exchangeRate.getTargetCurrencyCode(), exchangeRate);
    }
    return arrayCodeMap;
  }

}

