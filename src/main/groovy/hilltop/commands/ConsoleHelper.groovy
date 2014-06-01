
package hilltop.commands

import hilltop.App
import com.urbancode.anthill3.domain.persistent.Persistent;

class ConsoleHelper {
  def exit = { System.exit(0) }

  def quit(message, throwable = null) {
    if (message)
      System.err.println message

    if (!throwable) System.exit(1)
    throw throwable
  }
}
