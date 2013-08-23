
package hilltop.cli

class Cli {
  def app
  def writer
  def commands = [:]

  Cli(app, Closure config, writer = null) {
    this.app = app
    this.writer = writer ?: new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"))
    new CommandBuilder(commands, this.writer).configure(config)
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
    if (args.exactly > 0) {
      arguments = args.getNames().collect { "<${it}>" }.join(' ')
    }
    else if (args.minimum > 0) {
      arguments = "<${args.name}>..."
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
