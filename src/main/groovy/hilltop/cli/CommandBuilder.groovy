package hilltop.cli

class CommandBuilder {
  def commands
  def writer
  def current

  def CommandBuilder(commands, writer) {
    this.commands = commands
    this.writer = writer
  }

  def command(name, Closure... config) {
    def newCommand = createCommand(name)

    if (config) {

      def parent = current
      current = newCommand
      config.each { c ->
        c.delegate = this; c()
      }
      current = parent
    }
    newCommand
  }

  def describe(text) {
    current.description = text
    current
  }

  def execute(actions) {
    current.execute = actions
    current
  }

  def options(config) {
    current.config = config
    current
  }

  def arguments(Map details) {
    def args = current.arguments
    args.exactly = details['exactly'] ?: 0
    args.minimum = details['minimum'] ?: args.exactly
    if (details['name'])
      args.name = details['name']
    current
  }

  def quit(message, throwable = null) {
    if (message)
      writer << message + '\n'
    writer.flush()

    if (throwable)
      throw throwable
    System.exit(0)
  }

  private void configure(Closure... config) {
    if (this.commands.isEmpty())
      command(null, config)
  }

  private Command createCommand(name) {
    def command = current ?
      Command.Sub(current, name) : Command.Core()
    return this.commands[command.path] = command
  }
}
