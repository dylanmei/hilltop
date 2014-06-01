package hilltop

class Out {
  def writer
  def items = []

  def Out(writer) {
    this.writer = writer
  }

  def send(Map data) {
    items << data
  }

  def send(Iterable<Map> data) {
    data.each { send(it) }
  }

  def flush() {
    def data = items
    if (data.size() == 1) {
      data = data[0]
    }

    writer.write(data)
  }

  def echo(message) {
    items << [message: message]
  }

  def echo(name, Long value) {
    items << [name: value]
  }

  def echo(name, String value) {
    items << [name: value]
  }

  def echo(name, Iterable<String> values) {
    items << [name: values]
  }

  def echo(name, Iterator<String> iter) {
    def values = []
    while (iter.hasNext())
      values << iter.next()
    items << [name: values]
  }

  def echo(name, Closure closure) {
    items << ["fixme": "closure!"]
  }
}
