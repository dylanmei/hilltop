package hilltop

import spock.lang.*
import hilltop.anthill.*

class ConsoleWriterSpec extends Specification {
  def w, s
  def setup() {
    s = new StringWriter()
    w = new ConsoleWriter(10, new PrintWriter(s))
  }

  def 'write an object'() {
    when:
      w.write([
        id: 123,
        name: 'abc',
        value: 'xyz',
        fav_color: 'red',
        true: true,
        false: false,
      ])

    then:
      s.toString() =~ /Name      abc\n/
      s.toString() =~ /Value     xyz\n/
      s.toString() =~ /Fav Color red\n/
      s.toString() =~ /True      Yes\n/
      s.toString() =~ /False     No\n/
  }

  def 'write a collection'() {
    when:
      w.write([
        [id: 123, name: 'abc', value: 'light'],
        [id: 456, name: 'xyz', value: 'heavy'],
      ])

    then:
      s.toString() =~ /abc\n/
      s.toString() =~ /xyz\n/
  }

  def 'write a marked collection'() {
    when:
      w.write([
        [id: 123, name: 'abc', value: 'light', mark: true],
        [id: 456, name: 'xyz', value: 'heavy', mark: false],
      ])

    then:
      s.toString() =~ /\* abc\n/
      s.toString() =~ /  xyz\n/
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

  def 'write an object with a marked property collection'() {
    when:
      w.write([
        name: 'foods',
        values: [
          [id: 123, color: 'green', name: 'jelly', mark: true],
          [id: 456, color: 'red', name: 'pudding', mark: false],
        ]
      ])

    then:
      s.toString() =~ /Name      foods\n/
      s.toString() =~ /Values    \* jelly\n/
      s.toString() =~ /            pudding\n/
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

  def 'write an object with a property map'() {
    when:
      w.write([
        name: 'things',
        props: [
          [key: 'car', value: 'subaru'],
          [key: 'cat', value: 'grumpy'],
        ]
      ])

    then:
      s.toString() =~ /Name      things\n/
      s.toString() =~ /Props     car: subaru\n/
      s.toString() =~ /          cat: grumpy\n/
  }
}
