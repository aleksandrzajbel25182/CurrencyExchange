package update;

import exchangerate.repository.CurrenciesRepository;
import exchangerate.repository.ExchangeRateRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import javax.sql.DataSource;
import update.Source.CurrencyExchangeRateSource;
import update.dto.ExchageRateDto;

public class Updater {

  private CurrencyExchangeRateSource source;

  private CurrenciesRepository currenciesRepository;

  private ExchangeRateRepository exchangeRateRepository;

  private LocalDate date;

  private ExchageRateDto exchageRateDto;

  public Updater(DataSource dataSource, CurrencyExchangeRateSource source, LocalDate date) {

    this.currenciesRepository = new CurrenciesRepository(dataSource);
    this.exchangeRateRepository = new ExchangeRateRepository(dataSource);
    this.source = source;
    this.date = date;
  }

  private void update() {
    List<ExchageRateDto> exchageRateDto = source.get(date);
  }

  public void updateExchageRate() {

    List<ExchageRateDto> exchageRates = source.get(date);

    // 1. Generate a charCode map
    HashMap<String, ExchageRateDto> arrayCodeMap = new HashMap<>();
    for (ExchageRateDto exchageRate : exchageRates) {
      arrayCodeMap.put(exchageRate.getCharCode(), exchageRate);
    }

    // 2. For this array, request currency identifiers from the database
    //    String -charCode Integer - id
    HashMap<String, Integer> idByCode = currenciesRepository.getCurrenciesIdByCode(
        arrayCodeMap.keySet());

    var exchangeRatesToUpdate = new ArrayList<Pair<Integer, ExchageRateDto>>();
    var exchangeRatesToInsert = new ArrayList<ExchageRateDto>();

    for (Map.Entry<String, Integer> entry : idByCode.entrySet()) {
      ExchageRateDto currency = arrayCodeMap.get(entry.getKey());

      if (currency != null) {
        exchangeRatesToUpdate.add(new Pair<>(entry.getValue(), currency));
      } else {
        exchangeRatesToInsert.add(currency);
      }
    }

    for (Pair<Integer, ExchageRateDto> entry : exchangeRatesToUpdate) {

      System.out.println(
          "key/id: " + entry.getKey() + "\n" +
              "CharCode : " + entry.getValue().getCharCode() + "\n" +
              "Nominal : " + entry.getValue().getNominal() + "\n" +
              "Date :" + entry.getValue().getDate()
      );
    }
  }


}

