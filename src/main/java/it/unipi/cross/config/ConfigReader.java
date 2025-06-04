package it.unipi.cross.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigReader is a utility class for reading configuration properties from a
 * file.
 * It loads key-value pairs from a specified properties file and provides
 * methods
 * to retrieve configuration values as strings or integers.
 *
 * <p>
 * Example usage:
 * 
 * <pre>
 * ConfigReader config = new ConfigReader("config.properties");
 * String host = config.getString("db.host");
 * int port = config.getInt("db.port");
 * </pre>
 *
 * <p>
 * If the configuration file is not found or cannot be loaded, or if a value
 * cannot be parsed as an integer, a {@link ConfigException} is thrown.
 */
public class ConfigReader {

   private final String SERVER_CONFIG_FILE = "src/main/resources/server_config.properties";
   private final String CLIENT_CONFIG_FILE = "src/main/resources/client_config.properties";

   private final Properties properties = new Properties();

   public ConfigReader() {
   }

   /**
    * Loads properties from the specified file path into the {@code properties}
    * object.
    *
    * @param filePath the path to the properties file to be loaded
    * @throws IOException if an I/O error occurs while reading the file
    */
   public void loadFile(String filePath) throws IOException {
      try (InputStream in = new FileInputStream(filePath)) {
         properties.load(in);
      }
   }

   /**
    * Loads properties from the default server configuration file into the
    * {@code properties} object.
    *
    * @throws IOException if an I/O error occurs while reading the file
    */
   public void loadServer() throws IOException {
      try (InputStream in = new FileInputStream(SERVER_CONFIG_FILE)) {
         properties.load(in);
      }
   }

   /**
    * Loads properties from the default client configuration file into the
    * {@code properties} object.
    *
    * @throws IOException if an I/O error occurs while reading the file
    */
   public void loadClient() throws IOException {
      try (InputStream in = new FileInputStream(CLIENT_CONFIG_FILE)) {
         properties.load(in);
      }
   }

   /**
    * Retrieves the value associated with the specified key from the properties.
    * 
    * @param key the property key to look up
    * @return the value associated with the key, or an empty string if the key is
    *         not found
    */
   public String getString(String key) {
      return properties.getProperty(key, "");
   }

   /**
    * Retrieves the value associated with the specified key as an integer.
    * 
    * @param key the key whose associated value is to be returned as an integer
    * @return the integer value associated with the specified key, or -1 if the
    *         value is an empty string
    * @throws NumberFormatException if the value cannot be parsed as an integer
    */
   public int getInt(String key) throws NumberFormatException {
      String val = getString(key);
      if (val.equals("")) {
         return -1;
      }

      return Integer.parseInt(val);
   }

}
