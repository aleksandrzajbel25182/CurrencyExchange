package update.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ExchageRateDto {

  private LocalDate date;

  private List<CurrencyDto> currencies;
}
