package com.currencyexchage.utils;


import com.currencyexchage.model.CurrencyCB;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.json.JSONObject;

public class JsonParser {

  private static GsonBuilder gsonBuilder;

  private static final String URL = "https://www.cbr-xml-daily.ru/daily_json.js";

  public JsonParser() {
    gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(CurrencyCB.class, new CurrencyCBDeserializerJsonImpl());
  }


  /**
   * Converts an array of bytes to a string.
   *
   * @param url
   * @return a string of bytes
   */
  private String readBytes(String url) throws IOException {
    return new String(
        new URL(URL)
            .openStream()
            .readAllBytes()
    );
  }

  public List<CurrencyCB> Parser() {

    try {
      String json = readBytes(URL);

      JSONObject currenciesData = new JSONObject(json)
          .getJSONObject("Valute");

      return currenciesData.keySet()
          .stream()
          .map((currency) ->
              gsonBuilder.create().fromJson(
                  currenciesData.getJSONObject(currency)
                      .toString(),
                  CurrencyCB.class
              )
          )
          .toList();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}

