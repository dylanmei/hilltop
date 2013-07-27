package hilltop

class App {
  def config

  def App(String... args) {
    config = new ConfigLoader().load()


    new Cli('hilltop', {
      describe 'Anthill command-line utility'
      options {
        v longOpt: 'version', 'Provides the current hilltop version'
      }
      execute { params ->
        if (params.v) quit('Hilltop Version: 0.1')
      }

      command('config') {
        describe 'things to do with projects'
        options {
          _ longOpt: 'set', args: 2, argName: 'name=value', valueSeparator: '=', 'sets a configuration value'
          _ longOpt: 'remove', args: 1, argName: 'name', 'removes a configuration value'
          _ longOpt: 'get', args: 1, argName: 'name', 'gets a configuration value'
        }
        execute { params ->
          if (params.get) getConfigValue(params.get)
          if (params.set) setConfigValue(params.set, params.sets[1])
          if (params.remove) removeConfigValue(params.remove)
          new ConfigLoader().save(config)
        }
      }
    }).run(args)
  }

  def getConfigValue(name) {
    print "$name=${config.get(name)}\n"
  }

  def setConfigValue(name, value) {
    config.put(name, value)
  }

  def removeConfigValue(name) {
    config.remove(name)
  }
}
