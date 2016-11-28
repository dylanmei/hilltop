package hilltop.commands

import hilltop.anthill.AgentFinder
import hilltop.anthill.EnvironmentFinder
import com.urbancode.anthill3.domain.agent.*
import com.urbancode.anthill3.services.agent.*

class AgentCommands extends AnthillCommands {
  def AgentCommands(out) {
    super(out)
  }

  def list() {
    send work {
      def agents = finder(AgentFinder).all()

      agents.collect {[
        id: it.id,
        name: it.name,
        url: link_to(it),
      ]}
    }
  }

  def show(name) {
    send work {
      def agent = finder(AgentFinder).one(name)
      def manager = new AgentManagerClient()
      def environments = agent.serverGroups
      [
        id: agent.id,
        name: agent.name,
        url: link_to(agent),
        online: manager.getAgentStatus(agent).online,
        configured: agent.isConfigured,
        ignored: agent.isIgnored,
        description: agent.description ?: '',
        hostname: agent.hostname,
        environments: environments.collect {[
          id: it.id, name: it.name,
        ]},
      ]
    }
  }

  def open(name) {
    def agent = work {
      finder(AgentFinder).one(name)
    }

    browse link_to(agent)
  }

  def addEnvironment(name, environmentName) {
    work {
      def agent = finder(AgentFinder).one(name)
      def environment = finder(EnvironmentFinder).one(environmentName)
      environment.addServer(agent)
      statusln("$name added to $environmentName")
    }
  }

  def removeEnvironment(name, environmentName) {
    work {
      def agent = finder(AgentFinder).one(name)
      def environment = finder(EnvironmentFinder).one(environmentName)
      environment.removeServer(agent)
      statusln("$name removed from $environmentName")
    }
  }
}
