package cross.requests;

import com.google.gson.Gson;

public class MessageResponse extends Response{
   private int response;
   private String errorMessage;

   public MessageResponse(String s) {
      Gson gson = new Gson();
      MessageResponse r = gson.fromJson(s, MessageResponse.class);

      this.response = r.getResponse();
      this.errorMessage = r.getErrorMessage();
   }

   public MessageResponse() {}

   public int getResponse() {
      return response;
   }
   
   public void setResponse(int response) {
      this.response = response;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   public void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
   }
   
}
