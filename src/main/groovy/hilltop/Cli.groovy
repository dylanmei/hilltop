
package hilltop

class Cli {
  def app
  def commands = [:]
  def writer

  Cli(app, Closure... config) {
    this.app = app
    this.writer = new PrintWriter(System.out)
    new CommandBuilder(commands).configure(config)
    this
  }

  def run(String... args) {
    def name = ':'
    def command = commands[name]

    def builder = new CliBuilder(usage: 'temp')
    builder.with command.config

    def options = builder.parse(args)
    command.execute(options)
  }
}

class CommandBuilder {
  def commands
  def current

  def CommandBuilder(commands) {
    this.commands = commands
  }

  def configure(Closure... config) {
    command(null, config)
  }

  def command(name, Closure... config) {
    def newCommand = new Command(':')
    this.commands[':'] = newCommand

    if (config) {
      current = newCommand
      config.each { c ->
        c.delegate = this; c()
      }
    }
    newCommand
  }

  def description(text) {
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
    print message + '\n'
    if (throwable) throw throwable
    System.exit(0)
  }
}

class Command {
  def name
  def path
  def config
  def execute

  def Command(name) {
    this.name = name
    this.path = name
  }
}
