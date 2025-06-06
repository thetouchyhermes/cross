package it.unipi.cross.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class ConditionalMapTypeAdapter extends TypeAdapter<Map<String, Object>> {
   
   private final Set<String> integerKeys;

   public ConditionalMapTypeAdapter(String[] integerKeys) {
      this.integerKeys = Set.of(integerKeys);
   }

   @Override
   public void write(JsonWriter out, Map<String, Object> value) throws IOException {
      if (value == null) {
         out.nullValue();
         return;
      }

      out.beginObject();
      for (Map.Entry<String, Object> entry : value.entrySet()) {
         String key = entry.getKey();
         Object val = entry.getValue();

         out.name(key);

         if (integerKeys.contains(key)) {
            // Serialize as integer (without quotes)
            if (val instanceof Integer) {
               out.value((Integer) val);
            } else if (val instanceof String) {
               try {
                  int intValue = Integer.parseInt((String) val);
                  out.value(intValue);
               } catch (NumberFormatException e) {
                  out.value((String) val);
               }
            } else {
               out.value(String.valueOf(val));
            }
         } else {
            // Serialize as string
            out.value(String.valueOf(val));
         }
      }
      out.endObject();
   }

   @Override
   public Map<String, Object> read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
         in.nextNull();
         return null;
      }

      Map<String, Object> map = new LinkedHashMap<>();
      in.beginObject();

      while (in.hasNext()) {
         String key = in.nextName();

         if (integerKeys.contains(key)) {
            // Read as integer and keep as integer
            if (in.peek() == JsonToken.NUMBER) {
               int intValue = in.nextInt();
               map.put(key, intValue);
            } else {
               // Fallback: read as string
               map.put(key, in.nextString());
            }
         } else {
            // Read as string
            map.put(key, in.nextString());
         }
      }

      in.endObject();
      return map;
   }
}

// Usage example
class Example {
   public static void main(String[] args) {
      // Define which keys should be serialized as integers
      String[] integerKeys = { "age", "count", "id" };

      // Create Gson instance with custom adapter
      Gson gson = new GsonBuilder()
            .registerTypeAdapter(
                  new TypeToken<Map<String, Object>>() {
                  }.getType(),
                  new ConditionalMapTypeAdapter(integerKeys))
            .setPrettyPrinting()
            .create();

      // Test data
      Map<String, Object> data = new LinkedHashMap<>();
      data.put("name", "John");
      data.put("age", 25); // Integer value - will be serialized as integer
      data.put("city", "New York");
      data.put("count", "100"); // String value - will be parsed and serialized as integer
      data.put("id", 12345); // Integer value - will be serialized as integer
      data.put("description", "A test user");

      // Serialize
      String json = gson.toJson(data);
      System.out.println("Serialized JSON:");
      System.out.println(json);

      // Deserialize
      Type mapType = new TypeToken<Map<String, Object>>() {
      }.getType();
      Map<String, Object> deserializedData = gson.fromJson(json, mapType);

      System.out.println("\nDeserialized Map:");
      deserializedData
            .forEach((k, v) -> System.out.println(k + " = " + v + " (type: " + v.getClass().getSimpleName() + ")"));
   }
}
