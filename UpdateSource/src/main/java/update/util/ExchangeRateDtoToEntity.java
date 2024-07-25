package update.util;

import java.util.List;
import update.dto.ExchangeRateDto;

public interface ExchangeRateDtoToEntity<T> {

  List<T> toEntity(List<ExchangeRateDto> exchangeRateDto);


}
