package it.unipi.cross.json;

/**
 * Represents a response object for user management commands and others,
 * includes error message
 **/
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
