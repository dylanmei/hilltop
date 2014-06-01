
package hilltop.commands

import hilltop.anthill.RequestFinder
import com.urbancode.anthill3.services.build.*
import com.urbancode.anthill3.domain.buildrequest.*
import com.urbancode.anthill3.domain.buildlife.*

class RequestCommands extends AnthillCommands {
  def RequestCommands(out) {
    super(out)
  }

  def open(id) {
    def request = work {
      finder(RequestFinder).one(id as int)
    }

    browse link_to(request)
  }

  def show(id) {
    send work {
      def request = finder(RequestFinder).one(id as int)
      def project = request.project
      def workflow = request.workflow

      def propertyNames = []
      if (request.propertyNames) {
        propertyNames = request.propertyNames
      }

      [
        id: request.id,
        name: "Build Request $id",
        url: link_to(request),
        project: project.name,
        workflow: workflow.name,
        requester: "$request.requesterName ($request.requestSource.name)",
        status: request.status.toString(),
        buildlife: request.status == BuildRequestStatusEnum.BUILD_LIFE_CREATED ?
          request.buildLife.id.toString() : '',
        workflow_case: request.status == BuildRequestStatusEnum.STARTED_WORKFLOW ?
          request.workflowCase.id.toString() : '',
        properties: propertyNames.collect {
          [key: it, value: request.getPropertyValue(it)]
        },
      ]
    }
  }
}
