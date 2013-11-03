package hilltop.commands

import hilltop.anthill.EnvironmentFinder
import com.urbancode.anthill3.domain.agent.*
import com.urbancode.anthill3.services.agent.*
import com.urbancode.anthill3.domain.servergroup.*

class EnvironmentCommands extends AnthillCommands {
  def finder = Finder(EnvironmentFinder)

  def show(name) {
    work {
      def environment = finder.one(name)

      echo environment.name
      echo link_to(environment)

      if (environment.description)
        echo "Description", environment.description

      def agents = environment.agentArray
      def manager = new AgentManagerClient()

      echo "Agents", { line ->
        agents.each { a -> line.echo "${manager.getAgentStatus(a).online ? ' ' : '!'} ${a.name}" }
      }
    }
  }

  def open(name) {
    def environment = work {
      finder.one(name)
    }

    browse link_to(environment)
  }

  def list(groupName) {
    work {
      def environments
      if (!groupName) environments = finder.all()
      else {
        def group = finder.group(groupName)
        environments = group.serverGroupArray
      }
      environments.each {
        echo it.name, it.description ?: ''
      }
    }
  }
}
