package hilltop.commands

import hilltop.Config

@Mixin(ConsoleHelper)
class ColonyCommands {
  def config = new Config()

  def init() {

    def newConfig = """
version = '1'
projects {
  project {
    name = 'Project 1'
  },
  project {
    name = 'Project 2'
  }
}
"""

    ConfigSlurper slurper = new ConfigSlurper()
    ConfigObject colony = slurper.parse(newConfig)

    def file = new File("Colonyfile")
    file.withWriter{ writer ->
      colony.writeTo(writer)
    }


  }

//http://mrhaki.blogspot.com/2009/08/grassroots-groovy-configuration-with.html
//http://stackoverflow.com/questions/8394763/reading-config-file-with-nested-closures-with-groovys-configslurper
//http://www.redtoad.ca/ataylor/2013/01/creating-a-groovy-configobject-from-a-closure/

  def exec() {
    def file = new File('Colonyfile')
    if (!file.exists()) return

    def colony = new ConfigSlurper().parse(file.toURL())
    echo colony.dump()
    echo colony.projects[0].name
  }

  class ColonyScript extends Script {
    Closure closure
    def run() {
      closure.resolveStrategy = Closure.DELEGATE_FIRST
      closure.delegate = this
      closure.call()
    }
  }
}
