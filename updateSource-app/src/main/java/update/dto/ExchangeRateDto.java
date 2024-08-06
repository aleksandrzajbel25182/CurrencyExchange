/*
 * ExchangeRateDto.java        1.0 2024/08/01
 */
package update.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/**
 * A data transfer object representing exchange rate information between two currencies. Contains
 * details such as base currency code, target currency code, nominal value, currency name, exchange
 * rate value, unit rate, and date of the exchange rate.
 * <p>
 * This class is used to encapsulate exchange rate data fetched from various sources.
 *
 * @author Александр Зайбель
 * @version 1.0
 */
@Getter
@Setter
public class ExchangeRateDto {

  /**
   * ISO Character code of the base currency.
   */
  private String baseCurrencyCode;

  /**
   * ISO Character code of the target currency
   */
  private String targetCurrencyCode;

  /**
   * Nominal value of the currency.
   */
  private int nominal;

  /**
   * Name of the currency
   */
  private String name;

  /**
   * The value of the exchange rate
   */
  private double rate;

  /**
   * The rate for 1 unit of currency
   */
  private double vunitRate;

  /**
   * Date associated with the exchange rate.
   */
  private LocalDate date;

}
