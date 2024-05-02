package com.currencyexchage.utils;


import com.currencyexchage.model.Currency;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import org.json.JSONObject;

public class JsonParser {

  final String URL = "https://www.cbr-xml-daily.ru/daily_json.js";

  static class Currencys {

    String CharCode;
    String Name;

  }
  static class CurrencysDeserializerFromJson implements JsonDeserializer {

    @Override
    public Object deserialize(JsonElement jsonElement, Type type,
        JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject jObject = jsonElement.getAsJsonObject();
      int id = 0;
      String code = jObject.get("CharCode").getAsString();
      String fullName = jObject.get("Name").getAsString();
      String sign = jObject.get("CharCode").getAsString();
      return new Currency(id, code,fullName, sign);
    }
  }

  public void Parser() throws IOException {

    try {
      // Преобразует массив байт в строку.
      String json = new String(
          // Создаёт URL со ссылкой на API ЦБ.
          new URL("https://www.cbr-xml-daily.ru/daily_json.js")
              // Открывает поток получения данных.
              .openStream()
              // Считывает все данные в виде массива байт.
              .readAllBytes()
      );

      // Парсит JSON и получает данные валют.
      JSONObject currenciesData = new JSONObject(json)
          .getJSONObject("Valute");

      GsonBuilder gson = new GsonBuilder();
      gson.registerTypeAdapter(Currency.class , new CurrencysDeserializerFromJson());

//      Currency targetObject = gson.create().fromJson(, Currency.class);
//      System.out.println(targetObject.getId() + " " + targetObject.getFullName());
//
//       Получает коды валют из полученных данных.
      List<Currency> currencies = currenciesData.keySet()
          // Преобразовывает Set в Stream.
          .stream()
          // При помощи GSON переводит JSON строчку валюты к классу Currency.
          .map((currency) ->
              gson.create().fromJson(
                  currenciesData.getJSONObject(currency)
                      .toString(),
                  Currency.class
              )
          )// Преобразовывает Stream в List.
          .toList();

      // Выводит информацию в консоль.
      currencies.forEach((currency) ->
          System.out.println(currency.getId() + " (" + currency.getFullName() + ") - " + currency.getCode() )
      );
    } catch (Exception ignored) {
      ignored.printStackTrace();
    }
  }
}

