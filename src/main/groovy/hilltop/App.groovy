package hilltop

class App {
  def config

  def App(String... args) {
    config = new ConfigLoader().load()

    new Cli('hilltop', {
      describe 'An Anthill command-line utility'
      options {
        v longOpt: 'version', 'Gets the current Hilltop version'
      }
      execute { params ->
        if (params.v) quit('hilltop version: 0.1')
      }

      command('config') {
        describe 'List, set, or remove configuration values'
        options {
          _ longOpt: 'get', args: 1, argName: 'name', 'Gets a configuration value'
          _ longOpt: 'set', args: 2, argName: 'name=value', valueSeparator: '=', 'Sets a configuration value'
          _ longOpt: 'remove', args: 1, argName: 'name', 'Removes a configuration value'
        }
        execute { params ->
          def handler = new ConfigCommands(config)
          if (params.get) handler.get(params.get)
          else if (params.set) handler.set(params.set, params.sets[1])
          else if (params.remove) handler.remove(params.remove)
          else handler.list()
        }
      }

      command('projects') {
        def handler = new ProjectCommands(config)
        describe 'Working with Anthill projects'
        execute {
          if (!check_anthill_configuration())
            quit("Anthill configuration is incomplete")
        }

        command('list') {
          describe 'List Anthill projects'
          options {
            f longOpt: 'folder', args: 1, 'List Anthill projects in a specific folder'
            i longOpt: 'inactive', 'Includes inactive projects'
          }
          execute { params ->
            if (params.f) handler.list_folder(params.f, params.i)
            else handler.list(params.i)
          }
        }

        command('show') {
          describe 'Show details of an Anthill project'
          execute { params ->
            if (params.arguments())
              handler.show(params.arguments())
          }
        }

        command('open') {
          describe 'Launch an Anthill project in the browser'
          options {
            a longOpt: 'admin', 'Launch the administrative configuration page'
          }
          execute { params ->
            if (params.arguments())
              handler.open(params.arguments()[0], params.a)
          }
        }
      }
    }).run(args)
  }

  private Boolean check_anthill_configuration() {
    def ah = config.get('anthill')
    ah != null && !ah.api_token.isEmpty() && !ah.api_server.isEmpty()
  }
}
