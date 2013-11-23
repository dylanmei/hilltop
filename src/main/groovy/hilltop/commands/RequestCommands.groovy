
package hilltop.commands

import hilltop.anthill.RequestFinder
import com.urbancode.anthill3.services.build.*
import com.urbancode.anthill3.domain.buildrequest.*
import com.urbancode.anthill3.domain.buildlife.*

class RequestCommands extends AnthillCommands {
  def requestFinder = Finder(RequestFinder)

  def open(id) {
    def request = work {
      requestFinder.one(id as int)
    }

    browse link_to(request)
  }

  def show(id) {
    work {
      def request = requestFinder.one(id as int)
      def project = request.project
      def workflow = request.workflow

      echo request, label: "Build Request $id", uri: link_to(request)
      echo "Workflow", "$project.name $workflow.name"

      echo 'Status', request.status.toString()
      echo 'Requester', "$request.requesterName ($request.requestSource.name)"
      if (request.status == BuildRequestStatusEnum.BUILD_LIFE_CREATED)
        echo 'Buildlife', request.buildLife.id.toString()
      if (request.status == BuildRequestStatusEnum.STARTED_WORKFLOW) 
        echo 'WorkflowCase', request.workflowCase.id.toString()

      if (request.propertyNames) {
        echo "Properties", { line ->
          request.propertyNames.each { n -> line.echo "$n=${request.getPropertyValue(n)}" }
        }      
      }
    }
  }
}
