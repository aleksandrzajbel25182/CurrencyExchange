/*
 * Currency.java        1.0 2024/08/11
 */
package com.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * The Currency class is a simple class containing information about the currency. It has three
 * fields: id, code, and full name, which can be set when creating a class object.
 *
 * @author Александр Зайбель
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Currency {

  private int id;

  /**
   * ISO Character code of the currency.
   */
  @NonNull
  private String code;

  /**
   * Full name of the currency
   */
  @NonNull
  private String fullName;

}
