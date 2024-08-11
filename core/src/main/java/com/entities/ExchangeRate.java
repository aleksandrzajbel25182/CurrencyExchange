/*
 * ExchangeRate.java        1.0 2024/08/11
 */
package com.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * A class representing the exchange rate between two currencies. Contains the identifier, base
 * currency, target currency, exchange rate and date
 *
 * @author Александр Зайбель
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate {

  private int id;

  /**
   * The base currency to be exchanged.
   */
  @NonNull
  private Currency baseCurrencyId;

  /**
   * The target currency to be exchanged.
   */
  @NonNull
  private Currency targetCurrencyId;

  /**
   * The exchange rate between the base and target currencies.
   */
  @NonNull
  private BigDecimal rate;

  /**
   * Date on the exchange rate
   */
  @NonNull
  private LocalDate date;
}
