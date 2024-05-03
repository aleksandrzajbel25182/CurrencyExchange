package com.currencyexchage.utils;

import com.currencyexchage.model.CurrencyCB;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public class CurrencyCBDeserializerJsonImpl implements JsonDeserializer {

  @Override
  public Object deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject jObject = jsonElement.getAsJsonObject();
    String id = jObject.get("ID").getAsString();
    String numCode = jObject.get("NumCode").getAsString();
    String charCode = jObject.get("CharCode").getAsString();
    int nominal = jObject.get("Nominal").getAsInt();
    String name = jObject.get("Name").getAsString();
    double value = jObject.get("Value").getAsDouble();
    return new CurrencyCB(id, numCode,charCode,nominal,name,value);
  }

}
