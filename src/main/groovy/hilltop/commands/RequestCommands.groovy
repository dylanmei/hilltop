
package hilltop.commands

import hilltop.Config
import hilltop.finders.BuildFinder
import com.urbancode.anthill3.services.build.*
import com.urbancode.anthill3.domain.buildrequest.*
import com.urbancode.anthill3.domain.buildlife.*

@Mixin(ConsoleHelper)
@Mixin(AnthillHelper)
class RequestCommands {
  def config = new Config()
  def buildFinder = new BuildFinder({
    error { m -> quit m }
  })

  def open(id) {
    def settings = config.get('anthill')
    def url = work {
      def buildlife = buildFinder.request(id as int)
      "http://${settings.api_server}:8181/tasks/project/BuildRequestTasks/viewBuildRequest?buildRequestId=${id}"
    }
    browse url
  }

  def show(id) {
    work {
      def request = buildFinder.request(id as int)
      def project = request.project
      def workflow = request.workflow

      echo workflow.name
      echo "Project", project.name

      echo 'Status', request.status.toString()
      if (request.status == BuildRequestStatusEnum.BUILD_LIFE_CREATED)
        echo 'Buildlife', request.buildLife.id.toString()
      if (request.status == BuildRequestStatusEnum.STARTED_WORKFLOW) 
        echo 'WorkflowCase', request.workflowCase.id.toString()

      echo "Properties", { line ->
        request.propertyNames.each { n -> line.echo "$n=${request.getPropertyValue(n)}" }
      }      
    }
  }
}
