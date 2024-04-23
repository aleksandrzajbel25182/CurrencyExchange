package com.currencyexchage.model;

import java.math.BigDecimal;
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
}
