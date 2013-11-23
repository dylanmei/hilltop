
package hilltop.commands

import hilltop.App
import com.urbancode.anthill3.domain.persistent.Persistent;

class ConsoleHelper {
  def out = App.out
  def terminate = { System.exit(0) }

  def quit(message, throwable = null) {
    if (message)
      out.echo message
    out.flush()

    if (!throwable) terminate()
    throw throwable
  }

  def echo(String message) {
    out.echo(message)
  }

  def echo(String name, Long value) {
    out.echo(name, value.toString())
  }

  def echo(String name, String value) {
    out.echo(name, value)
  }

  def echo(Map map = [:], Persistent p) {
    out.echo 'id', p.id
    if (!map.containsKey('label'))
      out.echo 'label', p.name
    map.each {
      out.echo it.key, it.value
    }
  }

  def echo(String name, Iterable<String> values) {
    out.echo(name, values)
  }

  def echo(String name, Iterator<String> values) {
    out.echo(name, values)
  }

  def echo(String name, Closure closure) {
    out.echo(name, closure)
  }
}