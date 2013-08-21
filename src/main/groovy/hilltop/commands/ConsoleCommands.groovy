
package hilltop.commands

class ConsoleCommands {
  def writer = new PrintWriter(System.out)
  def terminate = { System.exit(0) }

  def echo(message) {
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
}