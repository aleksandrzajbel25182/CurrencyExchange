package update.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
/**
 * A class of currency pairs consisting of the base currency ,
 * the target currency and their exchange rate at face value
 * ExchangerRateDto accepts data from different sources.
 * */
public class ExchangeRateDto {

  /**
   * @value baseCurrencyCode - ISO Character code of the base currency
   */
  private String baseCurrencyCode;

  /**
   * @value targetCurrencyCode - ISO Character code of the target currency
   */
  private String targetCurrencyCode;

  /**
   * @value nominal - The currency of the nominal value
   */
  private int nominal;

  /**
   * @value name - Name of the currency
   */
  private String name;

  /**
   * @value rate - The rate for 1 unit of currency
   */
  private double rate;

  /**
   * @value vunitRate - The rate for 1 unit of currency
   */
  private double vunitRate;

  /**
   * @value date - Date on the exchange rate
   */
  private LocalDate date;

}
