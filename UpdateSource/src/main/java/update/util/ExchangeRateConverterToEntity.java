package update.util;

import exchangerate.model.ExchangeRate;
import exchangerate.repository.CurrenciesRepository;
import exchangerate.repository.ExchangeRateRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
import javax.sql.DataSource;
import update.dto.ExchangeRateDto;

public class ExchangeRateConverterToEntity implements ExchangeRateDtoToEntity<ExchangeRate> {

  private CurrenciesRepository currenciesRepository;

  private ExchangeRateRepository exchangeRateRepository;

  public ExchangeRateConverterToEntity(DataSource dataSource) {
    this.currenciesRepository = new CurrenciesRepository(dataSource);
    this.exchangeRateRepository = new ExchangeRateRepository(dataSource);
  }

  @Override
  public List<ExchangeRate> toEntity(List<ExchangeRateDto> exchangeRateDto) {
    List<ExchangeRate> exchangeRates = new ArrayList<>();
    for (ExchangeRateDto entry : exchangeRateDto) {
      var baseCurrencyiId = currenciesRepository.findByCode(entry.getBaseCurrencyCode());
      var targetCurrencyId = currenciesRepository.findByCode(entry.getTargetCurrencyCode());
      ExchangeRate exchangeRate = new ExchangeRate();
      exchangeRate.setBaseCurrencyId(baseCurrencyiId);
      exchangeRate.setTargetCurrencyId(targetCurrencyId);
      exchangeRate.setRate(BigDecimal.valueOf(entry.getRate()));
      exchangeRate.setDate(entry.getDate());
      exchangeRates.add(exchangeRate);
    }
    return exchangeRates;
  }

  public List<ExchangeRate> toEntity(ArrayList<Pair<Integer, ExchangeRateDto>> exchageRateDtoPair) {
    List<ExchangeRate> exchangeRates = new ArrayList<>();

    for (Pair<Integer, ExchangeRateDto> entry : exchageRateDtoPair) {
      ExchangeRate exchangeRate = exchangeRateRepository.findById(entry.getKey());
      exchangeRate.setRate(BigDecimal.valueOf(entry.getValue().getRate()));
      exchangeRate.setDate(entry.getValue().getDate());
      exchangeRates.add(exchangeRate);
    }

    return exchangeRates;
  }

}
