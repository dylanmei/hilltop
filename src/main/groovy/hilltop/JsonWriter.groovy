package hilltop

import groovy.json.*
import org.codehaus.groovy.runtime.FlushingStreamWriter

class JsonWriter {
  def inner
  def complete = false

  def JsonWriter(w = null) {
    this.inner = w ?: new PrintWriter(new FlushingStreamWriter(System.out))
  }

  def write(Map data) {
    write_json data
  }

  def write(Iterable<Map> data) {
    write_json data
  }

  private void write_json(data) {
    if (complete)
      System.err.println "JsonWriter.write called multiple times."
    complete = true
    def text = JsonOutput.toJson(data)
    inner.println JsonOutput.prettyPrint(text)
    inner.flush()
  }
}

