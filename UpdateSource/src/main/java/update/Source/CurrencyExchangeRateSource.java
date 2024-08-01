/*
 * CurrencyExchangeRateSource.java        1.0 2024/08/01
 */
package update.Source;

import java.time.LocalDate;

import java.util.List;

import update.dto.ExchangeRateDto;

/**
 * An interface representing a source for currency exchange rate data.
 *
 * @author Александр Зайбель
 * @version 1.0
 */
public interface CurrencyExchangeRateSource {

  /**
   * Retrieves the exchange rates for a given date.
   *
   * @param date the date for which exchange rates are requested.
   * @return a list of ExchangeRateDto objects containing exchange rate information.
   */
  List<ExchangeRateDto> get(LocalDate date);
}
