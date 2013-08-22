package hilltop.commands

import hilltop.finders.EnvironmentFinder
import com.urbancode.anthill3.domain.agent.*
import com.urbancode.anthill3.services.agent.*
import com.urbancode.anthill3.domain.servergroup.*

@Mixin(ConsoleCommands)
@Mixin(AnthillCommands)
class EnvironmentCommands {
  def config

  EnvironmentFinder finder = new EnvironmentFinder()

  def EnvironmentCommands(config) {
    this.config = config
  }

  def show(environmentName) {
    work {
      def environment = finder.environment(environmentName) {
        error { m -> quit m }
      }

      if (environment == null)
        echo "it's null"

      echo environment.name
      if (environment.description)
        echo "Description".padRight(40) + environment.description

      def agents = environment.agentArray
      AgentManagerClient ac = new AgentManagerClient()

      echo "Agents".padRight(40) + "${agents.collect { a -> a.name + (ac.getAgentStatus(a).online ? '' : ' (offline)')}.join('\n' + (' ') * 40)}"
    }
  }

  def list() {
    work {
      def environments = finder.all()
      environments.each {
        echo it.name.padRight(40) + (it.description ?: '')
      }
    }
  }
}
