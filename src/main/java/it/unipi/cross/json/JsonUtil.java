package it.unipi.cross.json;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import it.unipi.cross.data.Order;
import it.unipi.cross.data.Trade;

/**
 * Utility class for serializing and deserializing Java objects to and from JSON
 * files using Gson.
 */
public class JsonUtil {

      private static final String[] integerKeys = { "size", "price", "orderId" };
      private static final Gson gson = new GsonBuilder()
                  .registerTypeAdapter(
                              new TypeToken<Map<String, Object>>() {
                              }.getType(),
                              new ConditionalMapTypeAdapter(integerKeys))
                  .registerTypeAdapter(Trade.class,
                              new TradeTypeAdapter())
                  .registerTypeAdapter(Order.class, new OrderTypeAdapter())
                  .create();
      private static final Gson prettyGson = new GsonBuilder()
                  .registerTypeAdapter(
                              new TypeToken<Map<String, Object>>() {
                              }.getType(),
                              new ConditionalMapTypeAdapter(integerKeys))
                  .registerTypeAdapter(Trade.class,
                              new TradeTypeAdapter())
                  .registerTypeAdapter(Order.class, new OrderTypeAdapter())
                  .setPrettyPrinting()
                  .create();

      /**
       * Converts the given object to its JSON string representation.
       *
       * @param obj the object to be converted to JSON
       * @return a JSON string representing the given object
       */
      public static String toJson(Object obj) {
            return gson.toJson(obj);
      }

      /**
       * Deserializes the specified JSON string into an object of the specified class.
       *
       * @param json  the JSON string to deserialize
       * @param clazz the class of T
       * @param <T>   the type of the desired object
       * @return an object of type T from the JSON string
       */
      public static <T> T fromJson(String json, Class<T> clazz) {
            return gson.fromJson(json, clazz);
      }

      /**
       * Reads a JSON file and deserializes its content into an object of the
       * specified class.
       *
       * @param <T>   the type of the object to return
       * @param file  the JSON file to read from
       * @param clazz the class of T
       * @return an instance of T deserialized from the JSON file
       * @throws IOException if an I/O error occurs while reading the file
       */
      public static <T> T readFromFile(File file, Class<T> clazz) throws IOException {
            try (FileReader reader = new FileReader(file)) {
                  return prettyGson.fromJson(reader, clazz);
            }
      }

      /**
       * Serializes the given object to JSON and writes it to the specified file.
       *
       * @param file the file to which the JSON representation of the object will be
       *             written
       * @param obj  the object to serialize to JSON
       * @throws IOException if an I/O error occurs during writing
       */
      public static void writeToFile(File file, Object obj) throws IOException {
            try (FileWriter writer = new FileWriter(file)) {
                  prettyGson.toJson(obj, writer);
            }
      }

}
