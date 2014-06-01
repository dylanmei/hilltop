package hilltop.commands

import groovy.lang.*
import hilltop.colonies.*

class ColonyCommands extends AnthillCommands {
  def ColonyCommands(out) {
    super(out)
  }

  def init() {
    def newConfig = '''
project "Example", {
  folder "/Temp"
  description "Example Colonyfile Project"
  lifecycle   "Example Life-Cycle Model"
  environment "Example Environment Group"
}
workflow "build", {
  originating yes
}
workflow "deploy"
'''

    def file = new File('Colonyfile')
    if (file.exists())
      quit "File <$file.absolutePath> already exists!"

    file.withWriter {
      it.println '/* Created by hilltop 0.1 */'
      it.print newConfig
    }

    echo "File created <$file.absolutePath>"
  }

  def exec(noop) {
    if (!noop)
      quit "Running without --noop is not supported!"

    def file = new File('Colonyfile')
    if (!file.exists()) return

    def builder = new ColonyBuilder()
    def binding = new Binding([
      colony: builder,
      project: builder.&project,
      workflow: builder.&workflow,
      yes: true, no: false
    ])

    def shell = new GroovyShell(binding)
    shell.evaluate(file)

    work {
      def colony = builder.build()
      def project = colony.project
      echo 'Project', project.name
      echo 'Folder', project.folder.path
      echo 'Lifecycle', project.lifeCycleModel.name
      echo 'Environment', project.environmentGroup.name


    }
//    echo "Workflows", { line ->
//      workflows.each {
//        w -> line.echo "${w.name}, originating = $w.originating"
//      }
//    }   
  }
}
