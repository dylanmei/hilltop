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
