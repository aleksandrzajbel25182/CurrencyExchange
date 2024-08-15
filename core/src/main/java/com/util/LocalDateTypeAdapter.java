/*
 * LocalDateTypeAdapter.java        1.0 2024/08/15
 */
package com.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This class is a custom JSON serializer and deserializer for the {@link LocalDate} type. It uses a
 * specific date format ("yyyy-MM-dd") to serialize and deserialize the date.
 *
 * @author Александр Зайбель
 * @version 1.0
 */
public class LocalDateTypeAdapter implements JsonSerializer<LocalDate>,
    JsonDeserializer<LocalDate> {

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * The method takes a {@link LocalDate} object, the type of the source, and the serialization
   *
   * @returns a {@link JsonElement} representing the serialized date in the specified format.
   */
  @Override
  public JsonElement serialize(final LocalDate date, final Type typeOfSrc,
      final JsonSerializationContext context) {
    return new JsonPrimitive(date.format(formatter));
  }

  /**
   * The method takes a {@link JsonElement}, the type of the target, and the deserialization
   *
   * @returns a {@link LocalDate} object parsed from the JSON input using the specified format.
   */
  @Override
  public LocalDate deserialize(final JsonElement json, final Type typeOfT,
      final JsonDeserializationContext context) throws JsonParseException {
    return LocalDate.parse(json.getAsString(), formatter);
  }

}
