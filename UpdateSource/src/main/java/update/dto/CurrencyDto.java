package update.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrencyDto {

  private String charCode;

  private int nominal;

  private String name;

  private double value;

  private double vunitRate;

  private LocalDate date;

}
