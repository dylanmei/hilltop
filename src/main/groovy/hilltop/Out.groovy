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
}
