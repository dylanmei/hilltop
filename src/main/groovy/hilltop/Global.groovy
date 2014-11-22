package hilltop

class Global {
  def static exit = { System.exit(0) }

  def static quit(message) {
    if (message)
      System.err.println message
    System.exit(1)
  }
}
