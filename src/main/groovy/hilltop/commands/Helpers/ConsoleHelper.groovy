
package hilltop.commands

import static hilltop.Global.quit

class ConsoleHelper {

  def quit(message, throwable = null) {
    if (!throwable) quit(message)
    throw throwable
  }
}
