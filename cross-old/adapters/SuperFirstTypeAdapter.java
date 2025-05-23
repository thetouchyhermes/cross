package cross.adapters;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class SuperFirstTypeAdapter<T> extends TypeAdapter<T> {
   private final Gson gson;
   private final Class<T> clazz;

   public SuperFirstTypeAdapter(Gson gson, Class<T> clazz) {
      this.gson = gson;
      this.clazz = clazz;
   }

   private Map<String, Object> getRecursiveFieldMap(Class<?> currentClass, T value) throws IOException{
      if (currentClass == null || currentClass == Object.class) {
         return new LinkedHashMap<>();
      }
      Map<String, Object> fieldMap = getRecursiveFieldMap(currentClass.getSuperclass(), value);

      Field[] fields = currentClass.getDeclaredFields();
      for (Field field : fields) {
         field.setAccessible(true);
         try {
            fieldMap.put(field.getName(), field.get(value));
         } catch (IllegalAccessException e) {
            throw new IOException(e);
         }
      }
      
      return fieldMap;
   }

   @Override
   public void write(JsonWriter out, T value) throws IOException {
      out.beginObject();
      //Map<String, Object> fieldMap = new LinkedHashMap<>();
      Class<?> currentClass = clazz;

      Map<String, Object> fieldMap = getRecursiveFieldMap(currentClass, value);

      for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
         out.name(entry.getKey());
         gson.toJson(entry.getValue(), entry.getValue() != null ? entry.getValue().getClass() : Object.class, out);
      }

      out.endObject();
   }

   @Override
   public T read(JsonReader in) throws IOException {
      JsonObject jsonObject = JsonParser.parseReader(in).getAsJsonObject();
      return gson.fromJson(jsonObject, clazz);
   }

   public static <T> TypeAdapterFactory newFactory(Class<T> clazz) {
        return new TypeAdapterFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
                if (!clazz.isAssignableFrom(type.getRawType())) {
                    return null;
                }
                return (TypeAdapter<R>) new SuperFirstTypeAdapter<>(gson, clazz);
            }
        };
    }

}
