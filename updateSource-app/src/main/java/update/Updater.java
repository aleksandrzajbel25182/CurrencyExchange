/*
 * Updater.java        1.0 2024/09/03
 */
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

/**
 * The Updater class is responsible for updating currency exchange rates based on a specified
 * source. It interacts with repositories to update currency and exchange rate data in the
 * database.
 *
 * @author Александр Зайбель
 * @version 1.0
 */
public class Updater {

  private CurrencyExchangeRateSource source;

  private CurrenciesRepository currenciesRepository;

  private ExchangeRateRepository exchangeRateRepository;

  private LocalDate date;

  private ExchangeRateConverterToEntity exchangeRateConverterToEntity;

  private CurrencyConverterToEntity currencyConverterToEntity;

  private boolean featureFlag;

  /**
   * Constructs an Updater with the provided data source, currency exchange rate source, date, and
   * feature flag. Initializes necessary components for updating currency and exchange rate data.
   *
   * @param dataSource  The data source for accessing database resources.
   * @param source      The currency exchange rate source to retrieve exchange rate data.
   * @param date        The date for which the exchange rates should be updated.
   * @param featureFlag A flag indicating whether to update exchange rates.
   */
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

  /**
   * Updates the currency exchange rates based on the source data. If the featureFlag is set to
   * false, it calls the baseUpdate method to update exchange rates. Otherwise, it upserts new
   * currencies and updates existing exchange rates.
   */
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

  /**
   * The method `baseUpdate` updates exchange rate data in the database. Here are the steps it
   * performs:
   *
   * <ol>
   * <li>Generates a map `arrayCodeMap` to store exchange rates by their currency codes.</li>
   * <li>Retrieves currency identifiers from the database for the currency codes in `arrayCodeMap` and stores them in `idByCode`.</li>
   * <li>Checks for new currencies in `arrayCodeMap` that are not in the database and adds them to the Currency table.</li>
   * <li>Retrieves exchange rates from the database for the specified currency identifiers and date.</li>
   * <li>Divides the retrieved data into lists for updating existing exchange rates and adding new ones.</li>
   * <li>Updates existing exchange rates and adds new exchange rates to the database.</li>
   * </ol>
   *
   * <p>This method will be triggered when the `featureFlag` is set to `false`.
   * In this case, it will update the exchange rate data according to the described steps.</p>
   *
   * @param exchangeRates The list of ExchangeRateDto objects to update the exchange rates.
   */
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
      currenciesRepository.create(currenciesToInsertList);
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
      exchangeRateRepository.update(exchangeToUpdate);
    }
    // Make a check for an empty list
    if (exchangeRatesToInsert != null) {
      List<ExchangeRate> exchangeInsert = exchangeRateConverterToEntity.toEntity(
          exchangeRatesToInsert);
      exchangeRateRepository.create(exchangeInsert);
    }
  }

  /**
   * Generates a map where currency codes are keys and corresponding ExchangeRateDto objects are values.
   *
   * @param exchangeRates The list of ExchangeRateDto objects to create the map from.
   * @return A HashMap with currency codes as keys and ExchangeRateDto objects as values.
   */
  private HashMap<String, ExchangeRateDto> getCharCodeMap(List<ExchangeRateDto> exchangeRates) {
    HashMap<String, ExchangeRateDto> arrayCodeMap = new HashMap<>();
    for (ExchangeRateDto exchangeRate : exchangeRates) {
      arrayCodeMap.put(exchangeRate.getBaseCurrencyCode(), exchangeRate);
      arrayCodeMap.put(exchangeRate.getTargetCurrencyCode(), exchangeRate);
    }
    return arrayCodeMap;
  }

}

