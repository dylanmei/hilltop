package hilltop

import org.codehaus.groovy.runtime.FlushingStreamWriter

class ConsoleWriter {
  def inner
  def padding

  def ConsoleWriter(padding = 40, w = null) {
    this.padding = padding
    this.inner = w ?: new PrintWriter(new FlushingStreamWriter(System.out))
  }

  def write(Iterable<Map> data) {
    data.each {
      def name = it.name ?: '???'
      inner.println format_label(name)
    }
    inner.flush()
  }

  def write(Map data) {
    data.each { key, value ->
      write_key_value(key, value)
    }
    inner.flush()
  }

  private void write_key_value(key, Long value) {
    write_key_value(key, value.toString())
  }

  private void write_key_value(key, String value) {
    switch (key) {
      case 'id': // never print
        break

      case 'label':
      case 'version':
      case 'uri':
      case 'url':
        // print without labels
        inner.println value
        break

      default:
        def name = format_label(key)
        inner.println name.padRight(padding) + value
    }
  }

  private void write_key_value(key, Iterable<Map> values) {
    def iter = values.iterator();
    def head = format_label(key).padRight(padding)

    if (!iter.hasNext()) {
      inner.println head + 'None'
    }
    else {
      def item = iter.next()
      def name = item.name ?: '???'
      inner.println head + name
    }

    while (iter.hasNext()) {
      def item = iter.next()
      def name = item.name ?: '???'
      inner.println((' ' * padding) + name)
    }
  }

  private String format_label(s) {
    s.capitalize()
  }
}

