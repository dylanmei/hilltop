
package hilltop

class Cli {
  def app
  def writer
  def commands = [:]

  Cli(app, Closure config, writer = null) {
    this.app = app
    this.writer = writer ?: new PrintWriter(System.out)
    new CommandBuilder(commands, writer).configure(config)
    this
  }

  def run(String... args) {
    def path = ''
    def command = commands[path]
    def params = execute(command, args)
    while (!params.isEmpty()) {
      path = path + ':' + params.head()
      command = commands[path]

      if (!command) break;
      params = execute(command, params.tail())
    }

    writer.flush()
  }

  def execute(command, params) {
    def builder = createBuilder(command)
    def options = builder.parse(params)

    def newParams = []
    if (shouldShowHelp(command, options)) {
      if (command.description)
        writer << command.description + '\n'
      builder.footer = help(command)
      builder.usage()
    }
    else {
      if (command.execute)
        command.execute(options)
      newParams = options.arguments()
    }

    newParams
  }

  def usage(command) {
    def path = command.path
    def options = ' [<options>] '
    def commands = path == '' ?
      options : " ${path.split(':').tail().join(options)}$options"

    def arguments = ''
    def args = command.arguments
    def argName = args.name
    if (args.exactly > 0) {
      if (args.exactly == 1) arguments = "<${argName}>"
      else if (args.exactly == 2) arguments = "<${argName}1> <${argName}2>"
      else arguments = "<${argName}1>... <${argName}${args.exactly}>"
    }
    else if (args.minimum > 0) {
      arguments = "<${argName}>..."
    }

    "$app$commands$arguments"
  }

  def help(command) {
    def content = ''
    def commands = command.children

    if (!commands.isEmpty()) {
      content = 'commands:\n' << ''
      def padding = commands*.name.inject(0) { size, name -> name.size() > size ? name.size() : size }
      commands.each {
        content << "\u00A0 ${it.name.padRight(padding)}    $it.description\n"
      }
    }
    content.toString()
  }

  private CliBuilder createBuilder(command) {
    def builder = new CliBuilder(usage: usage(command))
    builder.writer = writer
    builder.with command.config ?: {}

    def options = builder.getOptions()
    if (!options.hasOption('help'))
      options.addOption('h', 'help', false, 'Usage information')
    builder
  }

  private Boolean shouldShowHelp(command, options) {
    if (options.help) return true
    def args = command.arguments

    if (args.minimum == 0)
      return false
    args.minimum > options.arguments().size()
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

class Command {
  def name
  def description
  def path
  def config
  def execute
  def children = []
  def arguments = new CommandArguments()

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

class CommandArguments {
  def name = 'arg'
  def exactly = 0
  def minimum = 0
}