package com.currencyexchage.utils;
import com.google.gson.Gson;
import java.io.IOException;

public class JsonConvert {
  public static <T> String jsonConvert(T enity) throws IOException {

    Gson gson = new Gson();
    String json = gson.toJson(enity);
    return json;
  }

}
