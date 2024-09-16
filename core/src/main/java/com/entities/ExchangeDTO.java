/*
 * Exchange.java        1.0 2024/08/11
 */
package com.entities;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A class representing a currency exchange with the amount to be exchanged and its conversion
 * result
 *
 * @author Александр Зайбель
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeDTO {

  /**
   * The base currency to be exchanged.
   */
  Currency baseCurrency;

  /**
   * The target currency to be exchanged.
   */
  Currency targetCurrency;

  /**
   * The exchange rate between the base and target currencies.
   */
  BigDecimal rate;

  /**
   * The amount of the base currency to be exchanged.
   */
  BigDecimal amount;

  /**
   * The result of converting the amount of the base currency based on the specified exchange rate.
   */
  BigDecimal convertedAmount;

}
