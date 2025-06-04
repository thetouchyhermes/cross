package it.unipi.cross.config;

import java.io.IOException;

/**
 * Exception thrown to indicate an error occurred while reading or processing configuration.
 * <p>
 * This exception extends {@link IOException} and is used to wrap configuration-related errors,
 * providing a consistent prefix in the exception message for easier identification.
 * </p>
 */
public class ConfigException extends IOException{
   public ConfigException(String message) {
      super("[ConfigReader] " + message);
   }

   public ConfigException(String message, Throwable cause) {
      super("[ConfigReader] " + message, cause);
   }
}
