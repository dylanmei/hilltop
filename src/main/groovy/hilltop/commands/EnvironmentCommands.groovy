package hilltop.commands

import hilltop.Config
import hilltop.finders.EnvironmentFinder
import com.urbancode.anthill3.domain.agent.*
import com.urbancode.anthill3.services.agent.*
import com.urbancode.anthill3.domain.servergroup.*

@Mixin(ConsoleHelper)
@Mixin(AnthillHelper)
class EnvironmentCommands {
  def config = new Config()
  EnvironmentFinder finder = new EnvironmentFinder()

  def show(environmentName) {
    work {
      def environment = finder.environment(environmentName) {
        error { m -> quit m }
      }

      echo environment.name
      if (environment.description)
        echo "Description", environment.description

      def agents = environment.agentArray
      def manager = new AgentManagerClient()

      echo "Agents", { line ->
        agents.each { a -> line.echo "${manager.getAgentStatus(a).online ? ' ' : '!'} ${a.name}" }
      }
    }
  }

  def list() {
    work {
      def environments = finder.all()
      environments.each {
        echo it.name, it.description ?: ''
      }
    }
  }
}
