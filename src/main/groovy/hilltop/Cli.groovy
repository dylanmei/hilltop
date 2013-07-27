
package hilltop

class Cli {
  def app
  def writer
  def commands = [:]

  Cli(app, Closure... config) {
    this.app = app
    this.writer = new PrintWriter(System.out)
    new CommandBuilder(commands, writer).configure(config)
    this
  }

  def run(String... args) {
    def command = commands['']
    def params = execute(command, args)
    while (!params.isEmpty()) {
      command = commands[':' + params.head()]

      if (!command) break;
      params = execute(command, params.tail())
    }

    writer.flush()
  }

  def execute(command, params) {
    def builder = new CliBuilder(usage: usage(command))
    builder.writer = writer
    builder.with command.config

    def options = builder.parse(params)
    if (!options) return []
    command.execute(options)
    return options.arguments()
  }

  def usage(command) {
    def path = command.path
    def options = ' [<options>]'
    def commands = path == '' ?
      options : " ${path.split(':').tail().join(options)}$options"
    "$app$commands"
  }
}

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
//    writer << "\nadding $command.path\n"
    return this.commands[command.path] = command
  }
}

class Command {
  def name
  def description
  def path
  def config
  def execute
  def children = []

  static Command Core() {
    new Command('')
  }

  static Command Sub(parent, name) {
    def child = parent.children.find { it.name == name }
    if (!child) {
      child = new Command(name, parent.path + ':' + name)
      parent.children << child
    }
    child
  }

  def Command(name, path = null) {
    this.name = name
    this.path = path ?: name
  }
}
