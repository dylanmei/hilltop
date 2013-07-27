package hilltop

class App {
  def App(String... args) {
    new Cli('hilltop', {
      describe 'Anthill command-line utility'
      options {
        v longOpt: 'version', 'Provides the current hilltop version'
      }
      execute { params ->
        if (params.v) quit('Hilltop Version: 0.1')
      }

      def app = this
      print "\n"

      command('config') {
        describe 'things to do with projects'
        options {
          _ longOpt: 'set', args: 2, argName: 'name=value', valueSeparator: '=', 'sets a configuration variable'
          _ longOpt: 'remove', args: 1, argName: 'name', 'removes a configuration variable'
        }
        execute { params ->
          if (params.set) setConfig(params.set, params.sets[1])
          if (params.remove) removeConfig(params.remove)
        }
      }
    }).run(args)
  }

  def setConfig(name, value) {
    print "config: set <$name>=$value\n"
  }

  def removeConfig(name) {
    print "config: remove <$name>\n"
  }
}
