
package hilltop.commands

import hilltop.Config
import hilltop.finders.BuildFinder
import hilltop.finders.WorkflowFinder
import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.services.build.*
import com.urbancode.anthill3.domain.buildrequest.*
import com.urbancode.anthill3.domain.buildlife.*

@Mixin(ConsoleHelper)
@Mixin(AnthillHelper)
class BuildCommands {
  def config = new Config()
  WorkflowFinder workflowFinder = new WorkflowFinder()
  BuildFinder buildFinder = new BuildFinder()

  def open(id) {
    def settings = config.get('anthill')
    def url = work {
      def buildlife = findBuildlife(id as int)
      "http://${settings.api_server}:8181/tasks/project/BuildLifeTasks/viewBuildLife?buildLifeId=${buildlife.id}"
    }
    browse url
  }

  def request(projectName, workflowName) {
    def request = work {
      def workflow = findWorkflow(projectName, workflowName)
      createRequest(workflow)
    }

    print "Created build request ${request.id} for $workflowName; Waiting for Buildlife..."
    submitRequest(request)

    def building = false; while (!building) {
      sleep 250; print '.'

      work {
        request = findRequest(request.id)

        if (request.status == BuildRequestStatusEnum.BUILD_LIFE_CREATED) {
          building = true
          echo "Buildlife ${request.buildLife.id} is available"
        }
      }
      if (request.status == BuildRequestStatusEnum.BUILD_LIFE_NOT_NEEDED) {
        echo "Buildlife is not needed"; break
      }
      if (request.status == BuildRequestStatusEnum.FAILED) {
        echo "Buildlife creation failed"; break
      }
    }
  }

  private void submitRequest(request) {
    def service = new BuildServiceImplClient()
    try {
      service.init()
      service.runBuild(request)
    }
    finally {
      service.shutdown()
    }
  }

  private BuildRequest createRequest(workflow) {
    if (!workflow.isOriginating())
      quit "${workflow.name} is not an originating workflow"

    def uow = workflow.unitOfWork
    def request = BuildRequest.createOriginatingRequest(
      workflow.buildProfile, uow.user, RequestSourceEnum.MANUAL, uow.user)
    request.forcedFlag = true
    request.unitOfWork = uow
    request.store()
    request
  }

  private Workflow findWorkflow(projectName, workflowName) {
    workflowFinder.workflow(projectName, workflowName) {
      alert { m -> echo m }
      error { m -> quit m }
    }
  }

  private BuildLife findBuildlife(id) {
    buildFinder.buildlife(id) {
      error { m -> quit m }
    }
  }

  private BuildRequest findRequest(id) {
    buildFinder.request(id) {
      error { m -> quit m }
    }
  }
}