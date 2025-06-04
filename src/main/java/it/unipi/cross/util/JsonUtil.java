package it.unipi.cross.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Utility class for serializing and deserializing Java objects to and from JSON files using Gson.
 */
public class JsonUtil {

   private static final Gson gson = new GsonBuilder().create();

   /**
    * Converts the specified object into its JSON representation.
    *
    * @param obj the object to be converted to JSON
    * @return a JSON string representing the specified object
    */
   public static String toJson(Object obj) {
      return gson.toJson(obj);
   }

   
   /**
    * Deserializes the specified JSON string into an object of the specified class.
    *
    * @param <T>   the type of the desired object
    * @param json  the JSON string to deserialize
    * @param clazz the class of T
    * @return an object of type T deserialized from the JSON string
    */
   public static <T> T readFromFile(File file, Class<T> clazz) throws IOException {
      try (FileReader reader = new FileReader(file)) {
         return gson.fromJson(reader, clazz);
      }
   }

   /**
    * Serializes the given object to JSON and writes it to the specified file.
    *
    * @param file the file to which the JSON representation of the object will be written
    * @param obj the object to serialize to JSON
    * @throws IOException if an I/O error occurs during writing
    */
   public static void writeToFile(File file, Object obj) throws IOException {
      try (FileWriter writer = new FileWriter(file)) {
         gson.toJson(obj, writer);
      }
   }
   
}
