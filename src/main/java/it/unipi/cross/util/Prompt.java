package it.unipi.cross.util;

public class Prompt {

   // ANSI codes for colors
   public static final String RESET = "\033[0m";
   public static final String GREEN = "\033[0;32m";
   public static final String RED = "\033[0;31m";

   private static int exitStatus = 0;

   public static void printStart() {
      System.out.print(GREEN + "CROSS" + RESET
            + " 3.0.0:\n an ex"
            + GREEN + "C" + RESET + "hange o"
            + GREEN + "R" + RESET + "der b"
            + GREEN + "O" + RESET + "ok"
            + GREEN + "S" + RESET + " "
            + GREEN + "S" + RESET + "ervice\n\n");
   }

   public static void printEnd() {
      System.out.print("\n\n------------------------------------------------------------\n"
            + "\nThank you for using " + GREEN + "CROSS" + RESET + "!\n  by " + GREEN + "Ernesto Cioli" + RESET
            + " @ Laboratorio di Reti | di.unipi\n\n");
   }

   public static void printHelp(boolean isRegistered, boolean isLogged) {
      System.out.print("\nUsage: <command> [args]\n\n");

      if (!isRegistered) {
         System.out.print("Useful commands :\n"
               + "  register <username> <password> \n\t\tusername must be unique\n"
               + "  help \n\t\tprints this page\n"
               + "  exit \n\t\tstops the program\n\n");
      } else if (!isLogged) {
         System.out.print("Useful commands :\n"
               + "  register <username> <password> \n\t\tusername must be unique\n"
               + "  updateCredentials <username> <currentpwd> <newpwd> \n\t\tnew password can't be equal to the last one\n"
               + "  login <username> <password>\n"
               + "  help \n\t\tprints this page\n"
               + "  exit \n\t\tstops the program\n\n");
      } else {
         System.out.print("Useful commands:\n"
               + "  logout <username>\n"
               + "  insertLimitOrder <type> <size> <price> \n\t\ttype must be \"ask\" or \"bid\"\n"
               + "  insertMarketOrder <type> <size> \n\t\ttype must be \"ask\" or \"bid\"\n"
               + "  insertStopOrder <type> <size> <price> \n\t\ttype must be \"ask\" or \"bid\"\n"
               + "  cancelOrder <orderId> \n\t\t order must not have already been fulfilled\n"
               + "  getPriceHistory <month>\n"
               + "  help \n\t\tprints this page\n"
               + "  exit \n\t\tstops the program\n\n");
      }
   }

   public static void newLine(String username) {
      System.out.print(GREEN + username + "@cross" + RESET + "> ");
   }

   public static void printError(Class<?> clazz, String message) {
      System.err.println(RED + "[" + clazz.getName() + "] " + message + RESET);
   }

   public static void exit(int status) {
      setExitStatus(status);
      System.exit(exitStatus);
   }

   public synchronized static int getExitStatus() {
      return exitStatus;
   }

   private synchronized static void setExitStatus (int status) {
      if (exitStatus == 0)
         exitStatus = status;
   }
}