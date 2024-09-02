/*
 * Notification.java        1.0 2024/08/15
 */
package com.services;

import com.entities.ExchangeRate;
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
   * The exchange rate
   */
  private ExchangeRate exchangeRate;

}
