package hilltop.commands

import groovy.lang.*
import hilltop.colonies.*
import com.urbancode.anthill3.domain.project.*
//import com.urbancode.anthill3.domain.plugin.PluginFactory

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

    println "File created <$file.absolutePath>"
  }

  def exec(noop) {

    def file = new File('Colonyfile')
    if (!file.exists())
      quit "There is no Colonyfile in the current directory."

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
      // def plugin = PluginFactory.getInstance().restoreForPluginId('com.urbancode.anthill3.plugin.Git')
      // echo plugin.class.name
      // project.sourceConfigType = plugin.class

      println 'Project', project.name
      println 'Folder', project.folder.path
      println 'Lifecycle', project.lifeCycleModel.name
      println 'Environment', project.environmentGroup.name
      // println 'SourceType', project.sourceConfigType.name

      if (!noop)
        project.store()
    }
//    echo "Workflows", { line ->
//      workflows.each {
//        w -> line.echo "${w.name}, originating = $w.originating"
//      }
//    }
  }
}
