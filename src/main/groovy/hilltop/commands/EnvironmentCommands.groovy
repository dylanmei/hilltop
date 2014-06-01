package hilltop.commands

import hilltop.anthill.EnvironmentFinder
import hilltop.anthill.EnvironmentGroupFinder
import com.urbancode.anthill3.domain.agent.*
import com.urbancode.anthill3.services.agent.*
import com.urbancode.anthill3.domain.servergroup.*

class EnvironmentCommands extends AnthillCommands {
  def EnvironmentCommands(out) {
    super(out)
  }

  def show(name) {
    send work {
      def environment = finder(EnvironmentFinder).one(name)
      def groups = finder(EnvironmentGroupFinder).fetch(environment)
      def agents = environment.agentArray
      def manager = new AgentManagerClient()

      return [
        id: environment.id,
        name: environment.name,
        url: link_to(environment),
        description: environment.description,
        groups: groups.collect {[
          id: it.id, name: it.name,
        ]},
        agents: agents.collect {[
          id: it.id, name: it.name, mark: manager.getAgentStatus(it).online,
        ]},
      ]
    }
  }

  def open(name) {
    def environment = work {
      finder(EnvironmentFinder).one(name)
    }

    browse link_to(environment)
  }

  def list(groupName) {
    send work {
      def environments
      if (!groupName) environments = finder(EnvironmentFinder).all()
      else {
        def group = groupFinder.one(groupName)
        environments = group.serverGroupArray
      }

      return environments.collect {[
        id: it.id,
        name: it.name,
        url: link_to(it),
        description: it.description,
      ]}
    }
  }
}
