
package hilltop.commands

import org.codehaus.groovy.runtime.FlushingStreamWriter

class ConsoleCommands {
  def writer = new PrintWriter(new FlushingStreamWriter(System.out))
  def terminate = { System.exit(0) }

  def echo(message) {
    writer.println message
  }

  def echo(name, value) {
    writer.println name.padRight(40) + value.toString()
  }

  def echo(name, Iterable<String> values) {
    echo(name, values.iterator())
  }

  def echo(name, Iterator<String> values) {
    def i = values.iterator();
    def line = name.padRight(40)
    if (i.hasNext())
    {
      line += i.next()
    }
    echo line
    while (i.hasNext()) {
      line = (' ' * 40) + i.next()
      echo line
    }
  }

  def echo(name, Closure closure) {
    writer.print name.padRight(40)
    closure(new EchoVisitor(writer: writer))
  }

  class EchoVisitor {
    def writer
    def line = 0

    def echo(String message) {
      if (line++ > 0)
        writer.print (' ' * 40)
      writer.println message
    }
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