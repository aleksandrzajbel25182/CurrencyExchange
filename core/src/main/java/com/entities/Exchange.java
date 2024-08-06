package com.entities;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exchange {
  Currency baseCurrency;

  Currency targetCurrency;

  BigDecimal rate;

  BigDecimal amount;

  BigDecimal convertToAmount;

}
