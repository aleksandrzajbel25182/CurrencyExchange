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
import java.util.List;
import java.util.stream.Collectors;
import update.Source.CBRFSource;
import update.dto.CurrencyDto;
import update.dto.ExchageRateDto;

public class Updater {

  private CBRFSource cbrfSource;
  private CurrenciesRepository currenciesRepository;

  private ExchangeRateRepository exchangeRateRepository;

  private List<Integer> listCurrencyId;

  private HashMap<Integer, CurrencyDto> hashMap;


  private ExchageRateDto exchageRateDto;

  public Updater() {

    this.currenciesRepository = new CurrenciesRepository(null);
    this.exchangeRateRepository = new ExchangeRateRepository(null);
    this.cbrfSource = new CBRFSource();

  }

  private void update() {
    exchageRateDto = cbrfSource.get(LocalDate.now());
    listCurrencyId = new ArrayList<>();
    updateCurrency(exchageRateDto);
//    updateExchageRate();

  }

  public void updateExchageRate() {

    List<String> arrayCharCode = exchageRateDto.getCurrencies().stream()
        .map(CurrencyDto::getCharCode)
        .collect(Collectors.toList());

    List<Integer> currencies = new ArrayList<>();
    for (String charCode : arrayCharCode) {
      int idCurrency = currenciesRepository.findCodeId(charCode);
      if (idCurrency != -1) {
        currencies.add(idCurrency);
        hashMap.put(idCurrency, getCurrencyByCharCode(exchageRateDto , charCode));
      } else {
        //create
      }
    }
    for(Integer id : currencies){
      var targerRate = exchangeRateRepository.findByDate(id, Date.valueOf(exchageRateDto.getDate()));
      if(targerRate.getRate().equals(hashMap.get(id).getValue())){

      }

    }


  }

  private void updateCurrency(ExchageRateDto exchageRateDto) {

    for (CurrencyDto currencyDto : exchageRateDto.getCurrencies()) {

      int idCurrency = currenciesRepository.findCodeId(currencyDto.getCharCode());
      if (idCurrency != -1) {
        //
      } else {
        Currency curency = new Currency();
        curency.setCode(currencyDto.getCharCode());
        curency.setFullName(currencyDto.getName());
        currenciesRepository.create(curency);
      }
    }
  }

  public static CurrencyDto getCurrencyByCharCode(ExchageRateDto dto, String charcode) {
    for (CurrencyDto currency : dto.getCurrencies()) {
      if (currency.getCharCode().equals(charcode)) {
        return currency;
      }

    }
    return null;
  }


}

