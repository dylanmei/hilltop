
package hilltop.commands

class ConsoleCommands {
  def writer = new PrintWriter(System.out)
  def terminate = { System.exit(0) }

  def echo(message) {
    //writer.println truncate(message, 240)
    writer.println message
    writer.flush()
  }

  def quit(message, throwable = null) {
    if (message)
      writer.println message
    writer.flush()

    if (!throwable) terminate()
    throw throwable
  }

  def browse(url) {
    Class<?> d = Class.forName("java.awt.Desktop");
    d.getDeclaredMethod("browse", [java.net.URI.class] as Class[]).invoke(
    d.getDeclaredMethod("getDesktop").invoke(null), [java.net.URI.create(url)] as Object[]);
  }

  /*
  // http://stackoverflow.com/questions/3597550/ideal-method-to-truncate-a-string-with-ellipsis
  private final static String NON_THIN = "[^iIl1\\.,']";

  private static int textWidth(String str) {
      return (int) (str.length() - str.replaceAll(NON_THIN, "").length() / 2);
  }

  private static String truncate(String text, int max) {

      if (textWidth(text) <= max)
          return text;

      // Start by chopping off at the word before max
      // This is an over-approximation due to thin-characters...
      int end = text.lastIndexOf(' ', max - 3);

      // Just one long word. Chop it off.
      if (end == -1)
          return text.substring(0, max-3) + "...";

      // Step forward as long as textWidth allows.
      int newEnd = end;
      while (true) {
          end = newEnd;
          newEnd = text.indexOf(' ', end + 1);

          // No more spaces.
          if (newEnd == -1)
              newEnd = text.length();
        if (textWidth(text.substring(0, newEnd) + "...") >= max) break;
      } 

      return text.substring(0, end) + "...";
  }
  */
}