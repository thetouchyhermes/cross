package it.unipi.cross.client;

public class Prompt {

   // ANSI codes for colors
   public static final String RESET = "\033[0m";
   public static final String GREEN = "\033[0;32m";
   public static final String RED = "\033[0;31m";

   public static void printStart() {
      System.out.print(GREEN + "CROSS" + RESET
            + " 3.0.0:\n an ex"
            + GREEN + "C" + RESET + "hange o"
            + GREEN + "R" + RESET + "der b"
            + GREEN + "O" + RESET + "ok"
            + GREEN + "S" + RESET + " "
            + GREEN + "S" + RESET + "ervice\n\n");
      System.out.println("Use \"help()\" for list of commands\n");
   }

   public static void printEnd() {
      System.out.print("\n\n------------------------------------\n"
            + "\nThank you for using " + GREEN + "CROSS" + RESET + "!\n  by " + GREEN + "Ernesto Cioli" + RESET
            + " @ Laboratorio di Reti | di.unipi\n\n");
   }

   public static void printHelp() {
      System.out.print("\nUsage: <command>(<args>)\n\n");

      System.out.print("Available commands:\n"
            + "  register(username, password)\n"
            + "  updateCredentials(username, currentPassword, newPassword)\n"
            + "  login(username, password)\n"
            + "  logout()\n"
            + "  insertLimitOrder(type, size, limitPrice\n"
            + "  insertMarketOrder(type, size)\n"
            + "  insertStopOrder(type, size, stopPrice)\n"
            + "  cancelOrder(orderID)\n"
            + "  getPriceHistory(month)\n"
            + "  help() \n\t\tprints this page\n"
            + "  exit() \n\t\tstops the program\n\n");

      System.out.print("For more information about a specific command, type \"help(<command>)\"\n\n");
   }

   public static void printHelp(String command) {
      switch (command) {
         case "register":
            System.out.print("\nUsage: \n\t"
                  + "register(username, password) \n"
                  + "\t\tmust not be logged \n"
                  + "\t\tusername must be unique\n\n");
            break;
         case "updateCredentials":
            System.out.print("\nUsage: \n\t"
                  + "updateCredentials(username, currentPassword, newPassword) \n"
                  + "\t\tmust not be logged \n"
                  + "\t\tnew password can't be equal to the last one\n\n");
            break;
         case "login":
            System.out.print("\nUsage: \n\t"
                  + "login(username, password) \n"
                  + "\t\tmust not be logged\n\n");
            break;
         case "logout":
            System.out.print("\nUsage: \n\t"
                  + "logout() \n"
                  + "\t\tlogs out and stops the program\n\n");
            break;
         case "insertLimitOrder":
            System.out.print("\nUsage: \n\t"
                  + "insertLimitOrder(type, size, limitPrice) \n"
                  + "\t\ttype must be \"ask\" or \"bid\"\n\n"
                  + "\t\tsize and limitPrice must be integer\n");
            break;
         case "insertMarketOrder":
            System.out.print("\nUsage: \n\t"
                  + "insertMarketOrder(type, size) \n"
                  + "\t\ttype must be \"ask\" or \"bid\"\n\n"
                  + "\t\tsize must be integer\n");
            break;
         case "insertStopOrder":
            System.out.print("\nUsage: \n\t"
                  + "insertStopOrder(type, size, stopPrice) \n"
                  + "\t\ttype must be \"ask\" or \"bid\"\n\n"
                  + "\t\tsize and stopPrice must be integer\n");
            break;
         case "cancelOrder":
            System.out.print("\nUsage: \n\t"
                  + "cancelOrder(orderID) \n"
                  + "\t\torder must not have already been fulfilled\n\n"
                  + "\t\torderID must be integer\n");
            break;
         case "getPriceHistory":
            System.out.print("\nUsage: \n\t"
                  + "getPriceHistory(month) \n"
                  + "\t\tmonth must be of format \"MMYYYY\"\n\n");
            break;
         case "help":
            System.out.print("\nUsage: \n\t"
                  + "help() \n"
                  + "\t\tprints this page\n\n");
            break;
         case "exit":
            System.out.print("\nUsage: \n\t"
                  + "exit() \n"
                  + "\t\tstops the program\n\n");
            break;
         default:
            printHelp();
      }
   }

   public static void newLine(String username) {
      System.out.print(GREEN + username + "@cross" + RESET + "> ");
   }

   public static void printError(String message) {
      System.err.println(RED + message + RESET);
   }

}