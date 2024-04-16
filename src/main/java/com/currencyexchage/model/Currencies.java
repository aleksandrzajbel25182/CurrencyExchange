package com.currencyexchage.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Currencies {

  private int id;

  @NonNull
  private String code;

  @NonNull
  private String fullName;

  @NonNull
  private String sign;
}
