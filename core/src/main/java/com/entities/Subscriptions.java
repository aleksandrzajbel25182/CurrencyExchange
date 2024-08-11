/*
 * Subscriptions.java        1.0 2024/08/11
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
 * Represents a subscription for currency exchange rates.
 *
 * @author Александр Зайбель
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subscriptions {

  private int id;

  /**
   * The URL associated with the subscription.
   */
  private String url;

  /**
   * The base currency for the subscription.
   */
  private Currency baseCurrencyId;

  /**
   * The target currency for the subscription.
   */
  private Currency targetCurrencyId;

  /**
   * The exchange rate for the subscribed currencies.
   */
  private BigDecimal rate;

  /**
   * Date of the exchange rate
   */
  private LocalDate date;

  /**
   * The status of the subscription. Shipped/NOT sent
   */
  private String status;
}
