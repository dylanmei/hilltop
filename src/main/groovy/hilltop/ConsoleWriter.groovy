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
    def markable = markable_key(data)
    data.each {
      inner.println format_item(it, markable)
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

  private void write_key_value(key, Boolean value) {
    write_key_value(key, value ? 'Yes' : 'No')
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
    def markable = markable_key(values)

    if (!iter.hasNext()) {
      inner.println head + 'None'
    }
    else {
      def item = iter.next()
      inner.println head + format_item(item, markable)
    }

    while (iter.hasNext()) {
      def item = iter.next()
      inner.println((' ' * padding) + format_item(item, markable))
    }
  }

  private String markable_key(Iterable<Map> values) {
    if (values == null || values.empty) return ''
    def item = values.first()
    def keys = item.keySet().findAll {
      item[it].getClass() == Boolean
    }
    return keys.empty ? '' : keys.first()
  }

  private String format_label(s) {
    s.split('_')
     .collect { it.capitalize() }
     .join(' ')
  }

  private String format_item(item, markable) {
    def key = item.key
    if (key) {
      def value = item.value ?: 'Empty'
      "$key: $value"
    }
    else {
      def name = item.name ?: '???'
      if (markable != '')
        return (item[markable] ? '* ' : '  ') + name
      name
    }
  }
}

