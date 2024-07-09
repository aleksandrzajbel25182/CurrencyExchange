package update.Source;

import java.time.LocalDate;
import java.util.List;
import update.dto.ExchageRateDto;

public interface CurrencyExchangeRateSource {

  List<ExchageRateDto> get(LocalDate date);

}
