package com.services;

import com.entities.Currency;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

  private String message;

  private Currency baseCurrencyId;

  private Currency targetCurrencyId;

  private BigDecimal rate;

  private LocalDate date;

}
