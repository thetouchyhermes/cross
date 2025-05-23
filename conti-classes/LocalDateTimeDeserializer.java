package com;
import java.lang.reflect.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import com.google.gson.*;

/**
 * The LocalDateTimeDeserializer class is used to deserialize JSON strings into LocalDateTime objects.
 */
public class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
   private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm:ss a",
         Locale.ENGLISH);

   /**
    * Deserializes a JSON element into a LocalDateTime object.
    * @param json the JSON element to deserialize
    * @param type the type of the object to deserialize
    * @param context the context of the deserialization
    * @return the deserialized LocalDateTime object
    */
   @Override
   public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
      return LocalDateTime.parse(json.getAsString(), formatter);
   }
}
