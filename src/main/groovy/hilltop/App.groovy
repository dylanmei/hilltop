package hilltop

class App {
  def config

  def App(String... args) {
    config = new ConfigLoader().load()

    new Cli('hilltop', {
      describe 'an anthill command-line utility'
      options {
        v longOpt: 'version', 'the current hilltop version'
      }
      execute { params ->
        if (params.v) quit('hilltop version: 0.1')
      }

      command('config') {
        describe 'set and get configuration values'
        options {
          _ longOpt: 'get', args: 1, argName: 'name', 'gets a configuration value'
          _ longOpt: 'set', args: 2, argName: 'name=value', valueSeparator: '=', 'sets a configuration value'
          _ longOpt: 'remove', args: 1, argName: 'name', 'removes a configuration value'
        }
        execute { params ->
          def handler = new ConfigCommands(config)
          if (params.get) handler.get(params.get)
          if (params.set) handler.set(params.set, params.sets[1])
          if (params.remove) handler.remove(params.remove)
        }
      }

      command('projects') {
        describe 'anthill projects'
        options { h longOpt: 'help', 'todo' }

        command('list') {
          describe 'list anthill projects'
          options {
            f longOpt: 'folder', args: 1, 'gets the anthill projects in the folder'
            i longOpt: 'inactive', 'include inactive projects'
          }
          execute { params ->
            def handler = new ProjectCommands(config)
            if (params.f) handler.list_folder(params.f, params.i)
            else handler.list(params.i)
          }
        }
      }
    }).run(args)
  }
}
