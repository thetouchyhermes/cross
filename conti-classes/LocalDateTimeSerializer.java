package com;
import java.lang.reflect.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import com.google.gson.*;

/**
 * The LocalDateTimeSerializer class is used to serialize LocalDateTime objects into JSON strings.
 */
public class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
   private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm:ss a",
         Locale.ENGLISH);

   /**
    * Serializes a LocalDateTime object into a JSON element.
    * @param date the LocalDateTime object to serialize
    * @param type the type of the object to serialize
    * @param context the context of the serialization
    * @return the serialized JSON element
    */
   @Override
   public JsonElement serialize(LocalDateTime date, Type type, JsonSerializationContext context) {
      return new JsonPrimitive(date.format(formatter));
   }
}
