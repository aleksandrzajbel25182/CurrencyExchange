package update;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrencyDTO {

  private String charCode;

  private int nominal;

  private String name;

  private double value;

  private double vunitRate;

}
