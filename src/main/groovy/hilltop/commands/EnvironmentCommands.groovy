package hilltop.commands

import hilltop.Config
import hilltop.anthill.EnvironmentFinder
import com.urbancode.anthill3.domain.agent.*
import com.urbancode.anthill3.services.agent.*
import com.urbancode.anthill3.domain.servergroup.*

@Mixin(ConsoleHelper)
@Mixin(AnthillHelper)
class EnvironmentCommands {
  def config = new Config()
  def finder = new EnvironmentFinder({
    error { m -> quit m }
  })

  def show(name) {
    work {
      def environment = finder.environment(name)

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

  def open(name) {
    def settings = config.get('anthill')
    def url = work {
      def environment = finder.environment(name)
      "http://${settings.api_server}:8181/tasks/admin/servergroup/ServerGroupTasks/viewServerGroup?serverGroupId=${environment.id}"
    }
    browse url
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
