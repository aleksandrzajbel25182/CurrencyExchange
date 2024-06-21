package update.Source;

import java.time.LocalDate;
import java.util.List;
import update.dto.CurrencyDto;
import update.dto.ExchageRateDto;

public interface CurrencyExchangeRateSource {

  ExchageRateDto get(LocalDate date);

}
