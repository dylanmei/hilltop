package hilltop.commands

import hilltop.Config

@Mixin(ConsoleHelper)
class ColonyCommands {
  def config = new Config()

  def init() {

    def newConfig = """
colony {
  project {
    name = \"Project 1\"
    folder = \"/Services\"
    things = [
      'a', 'b', 'c'
    ]
  }
}"""

    ConfigSlurper slurper = new ConfigSlurper()
    ConfigObject colony = slurper.parse(newConfig)

    def file = new File("Colonyfile")
    file.withWriter{ writer ->
      colony.writeTo(writer)
    }    
  }

  def exec() {
    echo 'not implemented'
  }
}
