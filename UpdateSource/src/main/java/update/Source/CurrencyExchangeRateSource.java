package update.Source;

import java.time.LocalDate;
import java.util.List;
import update.CurrencyDTO;

public interface CurrencyExchangeRateSource {

  List<CurrencyDTO> get(LocalDate date);

}
