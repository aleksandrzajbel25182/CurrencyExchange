package com.currencyexchage.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonConvert {

  public static <T> String jsonConvert(T enity) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(enity);
    return json;
  }
}
