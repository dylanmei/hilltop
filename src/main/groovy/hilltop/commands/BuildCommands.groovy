
package hilltop.commands

import hilltop.finders.BuildFinder
import hilltop.finders.WorkflowFinder
import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.services.build.*
import com.urbancode.anthill3.domain.buildrequest.*
import com.urbancode.anthill3.domain.buildlife.*

@Mixin(ConsoleCommands)
@Mixin(AnthillCommands)
class BuildCommands {
  def config

  WorkflowFinder workflowFinder = new WorkflowFinder()
  BuildFinder buildFinder = new BuildFinder()

  def BuildCommands(config) {
    this.config = config
  }

  def open(id) {
    def settings = config.get('anthill')
    def url = work {
      def buildlife = findBuildlife(id)
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

    def thread = Thread.start {
      def complete = false
      while (!complete && !Thread.currentThread().isInterrupted()) {
        sleep 250
        print '.'

        work { uow ->
          request = BuildRequestFactory.getInstance().restore(request.id)
          if (request.status == BuildRequestStatusEnum.BUILD_LIFE_CREATED) {
            def build = request.buildLife
            echo "Buildlife ${build.id} is available"
            complete = true
          }
          if (request.status == BuildRequestStatusEnum.BUILD_LIFE_NOT_NEEDED) {
            echo "Buildlife is not needed"
            complete = true
          }
          if (request.status == BuildRequestStatusEnum.FAILED) {
            echo "Buildlife creation failed"
            complete = true
          }
        }
      }
    }

    thread.join()
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
}