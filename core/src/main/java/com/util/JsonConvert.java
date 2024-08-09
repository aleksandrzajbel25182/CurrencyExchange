package com.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDate;

public class JsonConvert {

  public static <T> String jsonConvert(T enity) {
    Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
        .setPrettyPrinting().create();
    String json = gson.toJson(enity);
    return json;
  }
}
