package update.util;

import exchangerate.model.ExchangeRate;
import exchangerate.repository.CurrenciesRepository;
import exchangerate.repository.ExchangeRateRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
import javax.sql.DataSource;
import update.dto.ExchageRateDto;

public class ExchangeRateController implements ExchangeRateDtoToEntity<ExchangeRate> {

  private CurrenciesRepository currenciesRepository;

  private ExchangeRateRepository exchangeRateRepository;

  public ExchangeRateController(DataSource dataSource) {
    this.currenciesRepository = new CurrenciesRepository(dataSource);
    this.exchangeRateRepository = new ExchangeRateRepository(dataSource);
  }

  @Override
  public List<ExchangeRate> toEntity(List<ExchageRateDto> exchageRateDto) {
    List<ExchangeRate> exchangeRates = new ArrayList<>();
    for (ExchageRateDto entry : exchageRateDto) {
      var baseCurrencyiId = currenciesRepository.findByCode(entry.getBaseCurrency());
      var targetCurrencyId = currenciesRepository.findByCode(entry.getCharCode());
      ExchangeRate exchangeRate = new ExchangeRate();
      exchangeRate.setBaseCurrencyId(baseCurrencyiId);
      exchangeRate.setTargetCurrencyId(targetCurrencyId);
      exchangeRate.setRate(BigDecimal.valueOf(entry.getValue()));
      exchangeRate.setDate(entry.getDate());
      exchangeRates.add(exchangeRate);
    }
    return exchangeRates;
  }

  public List<ExchangeRate> toEntity(ArrayList<Pair<Integer, ExchageRateDto>> exchageRateDtoPair) {
    List<ExchangeRate> exchangeRates = new ArrayList<>();

    for (Pair<Integer, ExchageRateDto> entry : exchageRateDtoPair) {
      ExchangeRate exchangeRate = exchangeRateRepository.findById(entry.getKey());
      exchangeRate.setRate(BigDecimal.valueOf(entry.getValue().getValue()));
      exchangeRate.setDate(entry.getValue().getDate());
      exchangeRates.add(exchangeRate);
    }

    return exchangeRates;
  }

}
