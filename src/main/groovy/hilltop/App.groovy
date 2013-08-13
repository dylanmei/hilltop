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
          arguments exactly: 1
          execute { p -> handler.get(p.arguments().first()) }
        }

        command('set') {
          describe 'Set onfiguration values; <property=value> ...'
          arguments minimum: 1
          execute { p -> handler.set(p.arguments()) }
        }

        command('remove') {
          describe 'Remove configuration values'
          arguments minimum: 1
          execute { p -> handler.remove(p.arguments()) }
        }

        command('list') {
          describe 'List the current configuration values'
          execute { handler.list() }
        }
      }

      command('project') {
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
          arguments exactly: 1, name: 'project'
          execute { p ->
            handler.show(p.arguments().first())
          }
        }

        command('open') {
          describe 'Launch an Anthill project in the browser'
          arguments exactly: 1, name: 'project'
          options {
            a longOpt: 'admin', 'Launch the administrative configuration page'
          }
          execute { p ->
            handler.open(p.arguments().first(), p.a)
          }
        }
      }

      command('workflow') {
        def handler = new WorkflowCommands(config)
        describe 'Working with Anthill workflows'
        execute {
          if (!check_anthill_configuration())
            quit("Your Anthill configuration requires anthill.api_server and anthill.api_token values.")
        }
        command('show') {
          describe 'Show details of an Anthill workflow'
          arguments exactly: 2
          execute { p ->
            handler.show(p.arguments()[0], p.arguments()[1])
          }
        }

        command('open') {
          describe 'Launch an Anthill workflow in the browser'
          arguments exactly: 2
          options {
            a longOpt: 'admin', 'Launch the administrative configuration page'
          }
          execute { p ->
            handler.open(p.arguments()[0], p.arguments()[1], p.a)
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
