package it.unipi.cross.json;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import it.unipi.cross.data.Order;

/**
 * Utility class for serializing and deserializing Java objects to and from JSON
 * files using Gson.
 */
public class JsonUtil {

      private static final String[] integerKeys = { "size", "price", "orderId" };

      private static final Gson gson = new GsonBuilder()
                  .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
                  }.getType(), new ConditionalMapTypeAdapter(integerKeys))
                  .registerTypeAdapter(Order.class, new OrderTypeAdapter())
                  .create();
      private static final Gson prettyGson = new GsonBuilder()
                  .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
                  }.getType(), new ConditionalMapTypeAdapter(integerKeys))
                  .registerTypeAdapter(Order.class, new OrderTypeAdapter())
                  .setPrettyPrinting()
                  .create();

      // Serializes the given object into a Json string
      public static <T> String toJson(T obj, boolean prettyPrinted) {
            if (prettyPrinted)
                  return prettyGson.toJson(obj);
            else
                  return gson.toJson(obj);
      }

      public static <T> String toJson(T obj) {
            return toJson(obj, false);
      }

      // Deserializes the given Json string into an object of the specified Type or
      // Class<T>
      public static <T> T fromJson(String json, Type type, boolean prettyPrinted) throws JsonSyntaxException {
            if (prettyPrinted)
                  return prettyGson.fromJson(json, type);
            else
                  return gson.fromJson(json, type);
      }

      public static <T> T fromJson(String json, Type type) throws JsonSyntaxException {
            return fromJson(json, type, false);
      }

      // Serializes the given object in Json format and writes it into the given file
      public static void writeToFile(Object obj, File file, boolean prettyPrinted) throws IOException {
            try (FileWriter writer = new FileWriter(file)) {
                  if (prettyPrinted)
                        prettyGson.toJson(obj, writer);
                  else
                        gson.toJson(obj, writer);
            }
      }

      public static void writeToFile(Object obj, File file) throws IOException {
            writeToFile(obj, file, true);
      }

      public static <T> void writeListToFile(File file, List<T> objects, boolean prettyPrinted) throws IOException {
            JsonUtil.writeToFile(objects, file, prettyPrinted);
      }

      public static <T> void writeListToFile(File file, List<T> objects) throws IOException {
            JsonUtil.writeToFile(objects, file, true);
      }

      // Reads JSON from the given file and deserializes it into an object of the
      // specified Type or Class<T>
      public static <T> T readFromFile(File file, Type type, boolean prettyPrinted) throws IOException {
            try (FileReader reader = new FileReader(file)) {
                  if (prettyPrinted)
                        return prettyGson.fromJson(reader, type);
                  else
                        return gson.fromJson(reader, type);

            }
      }

      public static <T> T readFromFile(File file, Type type) throws IOException {
            return readFromFile(file, type, true);
      }

      public static <T> List<T> readListFromFile(File file, Class<T> classOfT, boolean prettyPrinted) throws IOException {
            return readFromFile(file, TypeToken.getParameterized(List.class, classOfT).getType(), prettyPrinted);
      }

      public static <T> List<T> readListFromFile(File file, Class<T> classOfT) throws IOException {
            return readListFromFile(file, classOfT, true);
      }

}
