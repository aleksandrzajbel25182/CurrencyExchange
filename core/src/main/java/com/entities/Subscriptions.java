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
public class Subscriptions {

  private int id;

  private String url;

  private Currency baseCurrencyId;

  private Currency targetCurrencyId;

  private BigDecimal rate;

  private LocalDate date;

  private String status;
}
