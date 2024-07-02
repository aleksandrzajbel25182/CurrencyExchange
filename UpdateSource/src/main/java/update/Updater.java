package update;

import exchangerate.model.Currency;
import exchangerate.model.ExchangeRate;
import exchangerate.repository.CurrenciesRepository;
import exchangerate.repository.ExchangeRateRepository;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import update.Source.CBRFSource;
import update.Source.CurrencyExchangeRateSource;
import update.dto.CurrencyDto;
import update.dto.ExchageRateDto;

public class Updater {

  private CurrencyExchangeRateSource source;
  private CurrenciesRepository currenciesRepository;

  private ExchangeRateRepository exchangeRateRepository;

  private HashMap<Integer, CurrencyDto> currencyDtoHashMap;

  private LocalDate date;

  private ExchageRateDto exchageRateDto;

  public Updater(DataSource dataSource, CurrencyExchangeRateSource source, LocalDate date) {

    this.currenciesRepository = new CurrenciesRepository(dataSource);
    this.exchangeRateRepository = new ExchangeRateRepository(dataSource);
    this.source = source;
    this.date = date;
  }

  private void update() {
    exchageRateDto = source.get(date);

  }

  public void updateExchageRate() {

    exchageRateDto = source.get(date);

    // 1. Generate a charCode array
    List<String> arrayCharCode = new ArrayList<>();
    for (CurrencyDto curency : exchageRateDto.getCurrencies()) {
      arrayCharCode.add(curency.getCharCode());
    }

    // 2. For this array, request currency identifiers from the database
    HashMap<Integer, String> idsDataBase = currenciesRepository.currencuIds(arrayCharCode);

    // If the list from the database is empty, then enter it in the database
    if (idsDataBase.isEmpty()) {
      insertCurrency(exchageRateDto.getCurrencies());
    }

    // 3. Generate a HashMap id -> ObjectSource
    currencyDtoHashMap = new HashMap<>();

    for (Map.Entry<Integer, String> entry : idsDataBase.entrySet()) {
      String charCode = entry.getValue();
      for (CurrencyDto currency : exchageRateDto.getCurrencies()) {
        if (currency.getCharCode().equals(charCode)) {
          currencyDtoHashMap.put(entry.getKey(), currency);
          break;
        }
      }
    }




    // 4. ...

  }


  private void insertCurrency(List<CurrencyDto> currencies) {

    Currency currency = new Currency();
    List<Currency> newCurrencies = new ArrayList<>();
    for (CurrencyDto entity : currencies) {
      currency.setCode(entity.getCharCode());
      currency.setFullName(entity.getName());
      newCurrencies.add(currency);
    }

    currenciesRepository.createAll(newCurrencies);

  }

}

