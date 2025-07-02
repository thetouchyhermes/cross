package it.unipi.cross.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for reading configuration properties from a .properties file
 **/
public class ConfigReader {

   private final String SERVER_CONFIG_FILE = "src/main/resources/server_config.properties";
   private final String CLIENT_CONFIG_FILE = "src/main/resources/client_config.properties";

   private final Properties properties = new Properties();

   public ConfigReader() {
   }

   /** loads properties from the specified file path **/
   public void loadFile(String filePath) throws IOException {
      try (InputStream in = new FileInputStream(filePath)) {
         properties.load(in);
      }
   }

   /**
    * loads properties from the default server configuration file
    **/
   public void loadServer() throws IOException {
      try (InputStream in = new FileInputStream(SERVER_CONFIG_FILE)) {
         properties.load(in);
      }
   }

   /**
    * loads properties from the default client configuration file
    **/
   public void loadClient() throws IOException {
      try (InputStream in = new FileInputStream(CLIENT_CONFIG_FILE)) {
         properties.load(in);
      }
   }

   /**
    * Retrieves the string associated with the specified key from the properties,
    * or an empty string
    **/
   public String getString(String key) {
      return properties.getProperty(key, "");
   }

   /**
    * Retrieves the integer associated with the specified key from the properties,
    * or -1 or a NumberFormatException
    **/
   public int getInt(String key) throws NumberFormatException {
      String val = getString(key);
      if (val.equals("")) {
         return -1;
      }

      return Integer.parseInt(val);
   }

}
