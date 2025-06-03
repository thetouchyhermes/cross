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

   private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

   public static <T> T readFromFile(File file, Class<T> clazz) throws IOException {
      try (FileReader reader = new FileReader(file)) {
         return gson.fromJson(reader, clazz);
      }
   }

   public static void writeToFile(File file, Object obj) throws IOException {
      try (FileWriter writer = new FileWriter(file)) {
         gson.toJson(obj, writer);
      }
   }
}
