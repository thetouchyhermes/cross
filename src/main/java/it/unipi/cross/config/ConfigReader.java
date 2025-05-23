package it.unipi.cross.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigReader is a utility class for reading configuration properties from a file.
 * It loads key-value pairs from a specified properties file and provides methods
 * to retrieve configuration values as strings or integers.
 *
 * <p>Example usage:
 * <pre>
 *     ConfigReader config = new ConfigReader("config.properties");
 *     String host = config.getString("db.host");
 *     int port = config.getInt("db.port");
 * </pre>
 *
 * <p>If the configuration file is not found or cannot be loaded, or if a value
 * cannot be parsed as an integer, a {@link ConfigException} is thrown.
 */
public class ConfigReader {

   private final Properties properties = new Properties();

   /**
    * Constructs a ConfigReader that loads configuration properties from the specified file path.
    *
    * @param filePath the path to the configuration file to be loaded
    * @throws ConfigException if the configuration file is not found or an error occurs while loading it
    */
   public ConfigReader(String filePath) throws ConfigException {
      try (InputStream in = new FileInputStream(filePath)) {
         properties.load(in);
      }
      catch (FileNotFoundException e) {
         throw new ConfigException("Config file not found: " + filePath, e);
      } catch (IOException e) {
         throw new ConfigException("Error loading config file: " + filePath, e);
      }
   }

   /**
    * Retrieves the value associated with the specified key from the properties.
    * If the key is not found, an empty string is returned.
    *
    * @param key the property key to look up
    * @return the value associated with the key, or an empty string if the key does not exist
    */
   public String getString(String key) {
      return properties.getProperty(key, "");
   }

   /**
    * Retrieves the value associated with the specified key as an integer.
    * <p>
    * If the value is an empty string, returns -1. If the value cannot be parsed as an integer,
    * a {@link ConfigException} is thrown.
    * </p>
    *
    * @param key the configuration key whose associated value is to be returned
    * @return the integer value associated with the specified key, or -1 if the value is empty
    * @throws ConfigException if the value cannot be parsed as an integer
    */
   public int getInt(String key) throws ConfigException {
      String val = getString(key);
      if (val.equals("")) {
         return -1;
      }
      try {
         return Integer.parseInt(val);
      } catch (NumberFormatException e) {
         throw new ConfigException("Invalid integer for " + key + ": " + val, e);
      }
   }

}
