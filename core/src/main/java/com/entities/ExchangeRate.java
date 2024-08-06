package com.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate {

  private int id;
  @NonNull
  private Currency baseCurrencyId;
  @NonNull
  private Currency targetCurrencyId;
  @NonNull
  private BigDecimal rate;
  @NonNull
  private LocalDate date;
}
