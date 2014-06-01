package hilltop.commands

import hilltop.anthill.AgentFinder
import com.urbancode.anthill3.domain.agent.*

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
      [
        id: agent.id,
        name: agent.name,
        url: link_to(agent),
      ]
    }
  }

  def open(name) {
    def agent = work {
      finder(AgentFinder).one(name)
    }

    browse link_to(agent)
  }
}
