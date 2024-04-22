package com.currencyexchage.utils;

import com.google.gson.Gson;

public class JsonConvert {

  public static <T> String jsonConvert(T enity) {
    Gson gson = new Gson();
    String json = gson.toJson(enity);
    return json;
  }
}
