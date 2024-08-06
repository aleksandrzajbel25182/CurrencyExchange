package update;

import com.entities.Currency;
import com.entities.ExchangeRate;
import com.repository.CurrenciesRepository;
import com.repository.ExchangeRateRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import javax.sql.DataSource;
import update.source.CurrencyExchangeRateSource;
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

  private boolean featureFlag;
  public Updater(DataSource dataSource, CurrencyExchangeRateSource source, LocalDate date,
      boolean featureFlag) {
    this.currencyConverterToEntity = new CurrencyConverterToEntity();
    this.exchangeRateConverterToEntity = new ExchangeRateConverterToEntity(dataSource);
    this.currenciesRepository = new CurrenciesRepository(dataSource);
    this.exchangeRateRepository = new ExchangeRateRepository(dataSource);
    this.source = source;
    this.date = date;
    this.featureFlag = featureFlag;
  }

  public void updateExchageRate() {
    List<ExchangeRateDto> exchangeRateDto = source.get(date);
    if (!featureFlag) {
      baseUpdate(exchangeRateDto);
      return;
    }
    HashMap<String, String> charCodeAndNameMap = new HashMap<>();
    for (ExchangeRateDto exchangeRate : exchangeRateDto) {
      charCodeAndNameMap.put(exchangeRate.getBaseCurrencyCode(), exchangeRate.getName());
      charCodeAndNameMap.put(exchangeRate.getTargetCurrencyCode(), exchangeRate.getName());
    }
    List<Currency> currenciesToInsert = currencyConverterToEntity.toEntity(charCodeAndNameMap);
    currenciesRepository.upsert(currenciesToInsert);

    List<ExchangeRate> exchangeToUpdate = exchangeRateConverterToEntity.toEntity(exchangeRateDto);
    exchangeRateRepository.upsert(exchangeToUpdate);
  }

  private void baseUpdate(List<ExchangeRateDto> exchangeRates) {
    // 1. Generate a charCode map
    HashMap<String, ExchangeRateDto> arrayCodeMap = getCharCodeMap(exchangeRates);

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
    // 3.1 Creating a Map with currency pairs and ExchangeRateDto
    Map<Pair<Integer, Integer>, ExchangeRateDto> pairsCharCodesMap = new HashMap<>();
    for (ExchangeRateDto rate : exchangeRates) {
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
    var exchangeRatesToUpdate = new ArrayList<Pair<Integer, ExchangeRateDto>>();
    var exchangeRatesToInsert = new ArrayList<ExchangeRateDto>();
    for (Pair<Integer, Integer> pair : uniquePairs) {
      if (existingPairsMap.containsKey(pair)) {
        exchangeRatesToUpdate.add(
            new Pair<>(existingPairsMap.get(pair),
                pairsCharCodesMap.get(pair)));
      } else {
        exchangeRatesToInsert.add(pairsCharCodesMap.get(pair));
      }
    }
    if (exchangeRatesToUpdate != null) {
      List<ExchangeRate> exchangeToUpdate = exchangeRateConverterToEntity.toEntity(
          exchangeRatesToUpdate);
      exchangeRateRepository.updateBatch(exchangeToUpdate);
    }
    // Make a check for an empty list
    if (exchangeRatesToInsert != null) {
      List<ExchangeRate> exchangeInsert = exchangeRateConverterToEntity.toEntity(
          exchangeRatesToInsert);
      exchangeRateRepository.createBatch(exchangeInsert);
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

