/*
 * JsonConvert.java        1.0 2024/08/15
 */
package com.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

/**
 * Utility class for converting objects to JSON format.
 *
 * @author Александр Зайбель
 * @version 1.0
 */
public class JsonConvert {

  /**
   * Converts the given Java object to a JSON string representation.
   *
   * @param entity the object to be converted to JSON is usually the database entity
   * @return a JSON string representation of the input object
   */
  public static <T> String jsonConvert(T entity) {
    Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
        .setPrettyPrinting().create();
    String json = gson.toJson(entity);
    return json;
  }
}
