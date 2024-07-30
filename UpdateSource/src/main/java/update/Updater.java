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
    var currencyToInsert = new HashMap<String, String>();
    for (Map.Entry<String, ExchangeRateDto> entry : arrayCodeMap.entrySet()) {
      if (!idByCode.containsKey(entry.getKey())) {
        currencyToInsert.put(entry.getKey(), entry.getValue().getName());
      }
    }
    if (!currencyToInsert.isEmpty()) {
      var currenciesToInsertList = currencyConverterToEntity.toEntity(currencyToInsert);
      currenciesRepository.createBatch(currenciesToInsertList);
    }

    // 3. Extract the courses from the database according to the received ids for the date specified
    var exchangeRatesToUpdate = new ArrayList<Pair<Integer, ExchangeRateDto>>();
    var exchangeRatesToInsert = new ArrayList<ExchangeRateDto>();
    // 3.1 Creating a Map with currency pairs and ExchangeRateDto
    Map<Pair<Integer, Integer>, ExchangeRateDto> pairsCharCodesMap = new HashMap<>();
    for (ExchangeRateDto rate : exchageRates) {
      int baseId = idByCode.get(rate.getBaseCurrencyCode());
      int targetId = idByCode.get(rate.getTargetCurrencyCode());
      pairsCharCodesMap.put(new Pair<>(baseId, targetId), rate);
    }
    // 3.2 Get a list of unique identifier pairs
    List<Pair<Integer, Integer>> uniquePairs = new ArrayList<>(pairsCharCodesMap.keySet());

    // 3.3 Getting existing currency pair identifiers from the database
    Map<Pair<Integer, Integer>, Integer> existingPairsMap = exchangeRateRepository.getExchangeRateIdsByPairs(
        uniquePairs);

    // 4. Divide it into lists for updating and adding
    for (Pair<Integer, Integer> pair : uniquePairs) {
      if (existingPairsMap.containsKey(pair)) {
        exchangeRatesToUpdate.add(
            new Pair<>(existingPairsMap.get(pair),
                pairsCharCodesMap.get(pair)));
      } else {
        exchangeRatesToInsert.add(pairsCharCodesMap.get(pair));
      }
    }
  }

  private HashMap<String, ExchangeRateDto> getCharCodeMap(List<ExchangeRateDto> exchangeRates) {
    HashMap<String, ExchangeRateDto> arrayCodeMap = new HashMap<>();
    for (ExchangeRateDto exchangeRate : exchangeRates) {
      arrayCodeMap.put(exchangeRate.getBaseCurrencyCode(), exchangeRate);
      arrayCodeMap.put(exchangeRate.getTargetCurrencyCode(), exchangeRate);
    }
    return arrayCodeMap;
  }

}

