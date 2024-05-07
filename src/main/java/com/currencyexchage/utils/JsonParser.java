package com.currencyexchage.utils;


import com.currencyexchage.model.CurrencyCB;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        new URL(url)
            .openStream()
            .readAllBytes()
    );
  }

  public List<CurrencyCB> parserCurrency() {

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

  public Date parserDate() throws IOException {
    String json = readBytes(URL);
    JSONObject date = new JSONObject(json);
    String dateStr = date.getString("Date");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    try {
      return sdf.parse(dateStr);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }
}

