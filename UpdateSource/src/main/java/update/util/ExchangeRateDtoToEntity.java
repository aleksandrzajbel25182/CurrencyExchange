package update.util;

import java.util.List;
import update.dto.ExchageRateDto;

public interface ExchangeRateDtoToEntity<T> {

  List<T> toEntity(List<ExchageRateDto> exchageRateDto);


}
