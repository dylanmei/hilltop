package hilltop

import spock.lang.*
import hilltop.anthill.*

class ConsoleWriterSpec extends Specification {
  def w, s
  def setup() {
    s = new StringWriter()
    w = new ConsoleWriter(10, new PrintWriter(s))
  }

  def 'write a collection'() {
    when:
      w.write([
        [id: 123, name: 'abc', value: 'light'],
        [id: 456, name: 'xyz', value: 'heavy'],
      ])

    then:
      s.toString() =~ /Abc\n/
      s.toString() =~ /Xyz\n/
  }

  def 'write an object'() {
    when:
      w.write([
        id: 123,
        name: 'abc',
        value: 'xyz',
      ])

    then:
      s.toString() =~ /Name      abc\n/
      s.toString() =~ /Value     xyz\n/
  }

  def 'write an object with a property collection'() {
    when:
      w.write([
        name: 'foods',
        values: [
          [id: 123, color: 'green', name: 'jelly'],
          [id: 456, color: 'red', name: 'pudding'],
        ]
      ])

    then:
      s.toString() =~ /Name      foods\n/
      s.toString() =~ /Values    jelly\n/
      s.toString() =~ /          pudding\n/
  }

  def 'write an object with an empty property collection'() {
    when:
      w.write([
        name: 'foods',
        values: []
      ])

    then:
      s.toString() =~ /Name      foods\n/
      s.toString() =~ /Values    None\n/
  }
}
