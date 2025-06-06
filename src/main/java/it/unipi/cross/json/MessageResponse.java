package it.unipi.cross.json;

/**
 * Represents a response object containing a response code and an optional error
 * message.
 * <p>
 * This class is immutable and provides methods to access the response code and
 * error message.
 * It also overrides {@code toString()} to return a pretty-printed JSON
 * representation of the object.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * Response response = new Response(200, null);
 * System.out.println(response); // Prints JSON representation
 * </pre>
 */
public class MessageResponse extends Response {
   private final int response;
   private final String errorMessage;

   public MessageResponse(int response, String errorMessage) {
      this.response = response;
      this.errorMessage = errorMessage;
   }

   public int getResponse() {
      return response;
   }

   public String getErrorMessage() {
      return errorMessage;
   }
}
