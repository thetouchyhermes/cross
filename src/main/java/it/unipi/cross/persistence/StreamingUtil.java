package it.unipi.cross.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class StreamingUtil {

   public static <T> T readLastObject(File file, Type type) throws IOException {
      if (!file.exists() || file.length() == 0) {
         return null;
      }

      JsonElement lastObj = null;
      try (JsonReader reader = new JsonReader(new FileReader(file))) {
         reader.beginObject();
         while (reader.hasNext()) {
            String name = reader.nextName();
            if ("trades".equals(name)) {
               reader.beginArray();
               while (reader.hasNext()) {
                  lastObj = JsonParser.parseReader(reader);
               }
               reader.endArray();
            } else {
               reader.skipValue();
            }
         }
         reader.endObject();
      }

      Gson gson = new Gson();
      try {
         T objT = gson.fromJson(lastObj, type);
         return objT;

      } catch (JsonSyntaxException e) {
         System.out.println("[StreamingUtil] No trade from file");
         return null;
      }
   }

   public static <T> boolean isTypeOfFile(File file, Type type) throws IOException {
      return readLastObject(file, type) != null;
   }

   public static void checkJsonObjAndFormatFile(File file, JsonObject newFileJsonObject, String arrayName)
         throws IOException {

      Pattern REGEX_MATCH = Pattern.compile("\\s*\\{\\s*\"" + arrayName + "\"\\s*:\\s*\\[.*?]\\s*}\\s*",
            Pattern.DOTALL);

      String fileAsString = new String(Files.readAllBytes(file.toPath()));

      // check structure of file
      if (!REGEX_MATCH.matcher(fileAsString).matches()) {

         try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(newFileJsonObject, writer);
         } catch (IOException e) {
            throw e;
         } catch (Exception e) {
            System.err.println(e.getClass() + ": " + e.getMessage());
         }
      }
   }

   public static <T> void appendObjectToFile(File file, T obj, String arrayName) throws IOException {
      List<T> objList = new ArrayList<>();
      objList.add(obj);
      appendObjectToFile(file, objList, arrayName);
   }

   public static <T> void appendObjectToFile(File file, List<T> objList, String arrayName) throws IOException {

      if (!file.exists()) {
         file.createNewFile();
      }
      
      Map<String, List<T>> wrapperObj = new HashMap<>();
      wrapperObj.put(arrayName, new ArrayList<>());
      JsonObject jsonWrapper = new Gson().toJsonTree(wrapperObj).getAsJsonObject();
      checkJsonObjAndFormatFile(file, jsonWrapper, arrayName);

      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      File tempFile = new File(file.getAbsolutePath() + ".tmp");

      try (
            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
         String line = reader.readLine();
         boolean ended = false;

         while (line != null) {

            String next = reader.readLine();

            if (!ended) {
               int idx = line.lastIndexOf(']');
               if (idx != -1) {
                  ended = true;

                  String beforeBracket = line.substring(0, idx);
                  String afterBracket = line.substring(idx + 1);

                  if (!beforeBracket.isBlank()) {

                     // there are other things on the line of ]
                     writer.write(beforeBracket);
                     if (beforeBracket.trim().endsWith("}")) {
                        writer.write(",");
                     }
                     writer.newLine();
                  }

                  if (!objList.isEmpty()) {
                     // write the remaining lines of the new trade with indentation
                     for (int i = 0; i < objList.size(); i++) {
                        String jsonObj = gson.toJson(objList.get(i));

                        String indentedLine = "      " + jsonObj.replace("\n", "\n      ");
                        writer.write(indentedLine);
                        
                        if (i < objList.size() - 1) {
                           writer.write(",");
                        }
                        writer.newLine();
                     }
                  }

                  writer.write("   ");
                  writer.write("]");

                  if (!afterBracket.isEmpty()) {
                     writer.write(afterBracket);
                  }

               } else {
                  if (!line.isBlank()) {
                     writer.write(line);
                  }

                  if (next != null && next.contains("]") && !next.contains("[")) {

                     // the next line is the array end and not the start
                     if (!line.contains("[") && !line.isBlank()) {

                        // there were other trades in the list
                        int nextIdx = next.lastIndexOf("]");
                        if (nextIdx != -1 && next.substring(0, nextIdx).isBlank()) {
                           writer.write(',');
                        }

                     }
                  }
               }
               writer.newLine();
            } else {
               writer.write(line);
            }

            line = next;
         }
      }

      // Replace original file with temp file
      Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
   }

}
