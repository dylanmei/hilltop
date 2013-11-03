package hilltop.commands

import hilltop.anthill.AgentFinder
import com.urbancode.anthill3.domain.agent.*

class AgentCommands extends AnthillCommands {
  def finder = Finder(AgentFinder)

  def list() {
    work {
      def agents = finder.all()
      agents.each {
        echo it.name
      }
    }
  }

  def open(name) {
    def agent = work {
      finder.one(name)
    }

    browse link_to(agent)
  }
}
