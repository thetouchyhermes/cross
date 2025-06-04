package it.unipi.cross.persistence;

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
    * Reads a JSON file and deserializes its content into an object of the specified class.
    *
    * @param <T>   the type of the object to return
    * @param file  the file to read from
    * @param clazz the class of T
    * @return an instance of T deserialized from the JSON file
    * @throws IOException if an I/O error occurs while reading the file
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
