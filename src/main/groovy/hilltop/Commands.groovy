
package hilltop

class Commands {
  def quit(message, throwable = null) {
    if (message)
      println message + '\n'
    if (throwable)
      throw throwable
    System.exit(0)
  }
}
