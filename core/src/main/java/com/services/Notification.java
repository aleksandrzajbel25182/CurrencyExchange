/*
 * Notification.java        1.0 2024/08/15
 */
package com.services;

import com.entities.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a notification containing information about a currency exchange rate.
 *
 * @author Александр Зайбель
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

  /**
   * The message associated with the notification.
   */
  private String message;

  /**
   * The base currency ID for the exchange rate.
   */
  private Currency baseCurrencyId;

  /**
   * The target currency ID for the exchange rate.
   */
  private Currency targetCurrencyId;

  /**
   * The exchange rate between the base and target currencies.
   */
  private BigDecimal rate;

  /**
   * The date of the exchange rate.
   */
  private LocalDate date;

}
