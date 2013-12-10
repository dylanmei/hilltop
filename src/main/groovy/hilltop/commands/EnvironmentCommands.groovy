package hilltop.commands

import hilltop.anthill.EnvironmentFinder
import hilltop.anthill.EnvironmentGroupFinder
import com.urbancode.anthill3.domain.agent.*
import com.urbancode.anthill3.services.agent.*
import com.urbancode.anthill3.domain.servergroup.*

class EnvironmentCommands extends AnthillCommands {
  def environmentfinder = Finder(EnvironmentFinder)
  def groupFinder = Finder(EnvironmentGroupFinder)

  def show(name) {
    work {
      def environment = environmentfinder.one(name)
      echo environment, uri: link_to(environment)

      if (environment.description)
        echo "Description", environment.description

      def agents = environment.agentArray

      def groups = groupFinder.fetch(environment)
      echo "Groups", { line ->
        groups.each { g -> line.echo g.name }
      }

      def manager = new AgentManagerClient()
      echo "Agents", { line ->
        agents.each { a -> line.echo "${manager.getAgentStatus(a).online ? ' ' : '!'} ${a.name}" }
      }
    }
  }

  def open(name) {
    def environment = work {
      environmentfinder.one(name)
    }

    browse link_to(environment)
  }

  def list(groupName) {
    work {
      def environments
      if (!groupName) environments = environmentfinder.all()
      else {
        def group = groupFinder.one(groupName)
        environments = group.serverGroupArray
      }
      environments.each {
        echo it.name, it.description ?: ''
      }
    }
  }
}
