package update.Source;

import java.time.LocalDate;
import java.util.List;
import update.dto.ExchangeRateDto;

public interface CurrencyExchangeRateSource {

  List<ExchangeRateDto> get(LocalDate date);

}
