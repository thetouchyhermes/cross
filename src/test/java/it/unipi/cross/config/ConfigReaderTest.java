package it.unipi.cross.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ConfigReaderTest {

   private static final String TEST_CONFIG_FILE = "test_config.properties";
   private static final String INVALID_CONFIG_FILE = "nonexistent.properties";
   private static final String INVALID_INT_CONFIG_FILE = "test_invalid_int.properties";

   @BeforeAll
   static void setup() throws IOException {
      // Write a valid config file
      Properties props = new Properties();
      props.setProperty("db.host", "localhost");
      props.setProperty("db.port", "3306");
      props.setProperty("empty.value", "");
      try (FileOutputStream out = new FileOutputStream(TEST_CONFIG_FILE)) {
         props.store(out, "Test config");
      }

      // Write a config file with an invalid integer
      Properties invalidProps = new Properties();
      invalidProps.setProperty("db.port", "notANumber");
      try (FileOutputStream out = new FileOutputStream(INVALID_INT_CONFIG_FILE)) {
         invalidProps.store(out, "Invalid int config");
      }
   }

   @AfterAll
   static void teardown() throws IOException {
      Files.deleteIfExists(Path.of(TEST_CONFIG_FILE));
      Files.deleteIfExists(Path.of(INVALID_INT_CONFIG_FILE));
   }

   @Test
   void testLoadValidConfigFile() {
      assertDoesNotThrow(() -> new ConfigReader(TEST_CONFIG_FILE));
   }

   @Test
   void testFileNotFoundThrowsException() {
      assertThrows(ConfigException.class, () -> new ConfigReader(INVALID_CONFIG_FILE));
   }

   @Test
   void testGetStringReturnsCorrectValue() throws ConfigException {
      ConfigReader config = new ConfigReader(TEST_CONFIG_FILE);
      assertEquals("localhost", config.getString("db.host"));
      assertEquals("3306", config.getString("db.port"));
      assertEquals("", config.getString("not.present"));
   }

   @Test
   void testGetIntReturnsCorrectValue() throws ConfigException {
      ConfigReader config = new ConfigReader(TEST_CONFIG_FILE);
      assertEquals(3306, config.getInt("db.port"));
   }

   @Test
   void testGetIntOnEmptyValueReturnsMinusOne() throws ConfigException {
      ConfigReader config = new ConfigReader(TEST_CONFIG_FILE);
      assertEquals(-1, config.getInt("empty.value"));
   }

   @Test
   void testGetIntThrowsIfNotParsable() throws ConfigException, IOException {
      ConfigReader config = new ConfigReader(INVALID_INT_CONFIG_FILE);
      assertThrows(ConfigException.class, () -> config.getInt("db.port"));
   }

   private static final String SERVER_CONFIG_PATH = "src/test/resources/server_config.properties";
   private static final String CLIENT_CONFIG_PATH = "src/test/resources/client_config.properties";
   private static final String MISSING_CONFIG_PATH = "src/test/resources/missing_config.properties";

   @Test
   void testServerConfigLoad() throws ConfigException {
      ConfigReader config = new ConfigReader(SERVER_CONFIG_PATH);

      assertEquals("51293", config.getString("server.port"));
      assertEquals("localhost", config.getString("server.host"));
      assertEquals("users.json", config.getString("storage.users_file"));
      assertEquals("storicoOrdini.json", config.getString("storage.orders"));
      assertEquals("120", config.getString("timeout.seconds"));

      assertEquals(51293, config.getInt("server.port"));
      assertEquals(120, config.getInt("timeout.seconds"));
   }

   @Test
   void testClientConfigLoad() throws ConfigException {
      ConfigReader config = new ConfigReader(CLIENT_CONFIG_PATH);

      assertEquals("51293", config.getString("server.port"));
      assertEquals("localhost", config.getString("server.host"));
      assertEquals("ernesto", config.getString("username"));
      assertEquals("120", config.getString("timeout.seconds"));

      assertEquals(51293, config.getInt("server.port"));
      assertEquals(120, config.getInt("timeout.seconds"));
   }

   @Test
   void testMissingConfigThrowsException() {
      assertThrows(ConfigException.class, () -> new ConfigReader(MISSING_CONFIG_PATH));
   }

   @Test
   void testGetStringForMissingKeyReturnsEmptyString() throws ConfigException {
      ConfigReader config = new ConfigReader(SERVER_CONFIG_PATH);
      assertEquals("", config.getString("not.a.key"));
   }

   @Test
   void testGetIntForMissingKeyReturnsMinusOne() throws ConfigException {
      ConfigReader config = new ConfigReader(SERVER_CONFIG_PATH);
      assertEquals(-1, config.getInt("not.a.key"));
   }

   @Test
   void testGetIntThrowsOnNonInteger() throws IOException, ConfigException {
      // Create a temporary config file with a non-integer value
      String path = "src/test/resources/temp_invalid.properties";
      Files.write(Paths.get(path), "nonint.key=notanint\n".getBytes());

      ConfigReader config = new ConfigReader(path);
      assertThrows(ConfigException.class, () -> config.getInt("nonint.key"));
      Files.deleteIfExists(Paths.get(path));
   }
}