
package hilltop.commands

import org.codehaus.groovy.runtime.FlushingStreamWriter

class ConsoleHelper {
  def writer = new PrintWriter(new FlushingStreamWriter(System.out))
  def terminate = { System.exit(0) }

  def echo() {
    writer.println ''
  }

  def echo(message) {
    writer.println message
  }

  def echo(name, String value) {
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
    else line += 'None'
    echo line
    while (i.hasNext()) {
      line = (' ' * 40) + i.next()
      echo line
    }
  }

  def echo(name, Closure closure) {
    writer.print name.padRight(40)
    def visitor = new EchoVisitor(writer: writer)
    closure(visitor)
    if (visitor.line == 0) echo()
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
}