package hilltop

import org.codehaus.groovy.runtime.FlushingStreamWriter

class Out {
  def writer = new PrintWriter(new FlushingStreamWriter(System.out))

  def echo(message) {
    writer.println message
  }

  def echo(name, Long value) {
    echo(name, value.toString())
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
    if (visitor.line == 0) echo('')
  }

  def flush() {
    writer.flush()
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
}
