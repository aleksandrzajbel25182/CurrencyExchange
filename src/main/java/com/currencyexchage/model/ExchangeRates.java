package com.currencyexchage.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExchangeRates {

  private int id;
  @NonNull
  private Currencies baseCurrencyId;
  @NonNull
  private Currencies targetCurrencyId;
  @NonNull
  private BigDecimal rate;
}
