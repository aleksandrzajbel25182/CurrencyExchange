package update.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ExchageRateDto {

  private List<CurrencyDto> currencies;
}
