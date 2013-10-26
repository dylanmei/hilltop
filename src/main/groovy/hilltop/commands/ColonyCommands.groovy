package hilltop.commands

import groovy.lang.*
import hilltop.colonies.*

@Mixin(ConsoleHelper)
class ColonyCommands {
  def init() {

    def newConfig = """
project ""Search"", {
  folder ""/Services""
}
workflow ""build"", {
  originating yes
}
workflow ""deploy""
"""

    def file = new File('Colonyfile')
    file.withWriter {
      it.println '/* Created by hilltop 0.1 */'
      it.print newConfig
    }
  }

  def exec() {
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

    def colony = builder.build()
    def project = colony.project
    def workflows = colony.workflows

    echo 'Project', "${project.name}, folder = $project.folder"
    echo "Workflows", { line ->
      workflows.each {
        w -> line.echo "${w.name}, originating = $w.originating"
      }
    }    
  }
}
