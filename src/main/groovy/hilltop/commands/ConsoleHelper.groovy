
package hilltop.commands

import hilltop.App

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

  def echo(message) {
    out.echo(message)
  }

  def echo(name, Long value) {
    out.echo(name, value.toString())
  }

  def echo(name, String value) {
    out.echo(name, value)
  }

  def echo(name, Iterable<String> values) {
    out.echo(name, values)
  }

  def echo(name, Iterator<String> values) {
    out.echo(name, values)
  }

  def echo(name, Closure closure) {
    out.echo(name, closure)
  }
}