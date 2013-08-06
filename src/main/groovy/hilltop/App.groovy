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
        def handler = new ConfigCommands(config)
        describe 'Manage configuration values'
        command('get') {
          describe 'Get a configuration value'
          execute { p ->
            if (!p.arguments()) help
            handler.get(p.arguments().head())
          }
        }
        command('set') {
          describe 'Set configuration values; <property=value> ...'
          execute { p ->
            if (!p.arguments()) help
            handler.set(p.arguments())
          }
        }
        command('remove') {
          describe 'Remove configuration values'
          execute { p ->
            if (!p.arguments()) help
            handler.remove(p.arguments())
          }
        }
        command('list') {
          describe 'List the current configuration values'
          execute { handler.list() }
        }
      }

      command('projects') {
        def handler = new ProjectCommands(config)
        describe 'Working with Anthill projects'
        execute {
          if (!check_anthill_configuration())
            quit("Your Anthill configuration requires anthill.api_server and anthill.api_token values.")
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
