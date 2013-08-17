
package hilltop.commands

import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.source.*
import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.domain.buildrequest.*
import com.urbancode.anthill3.services.build.*

class BuildCommands extends AnthillCommands {
  def config

  def BuildCommands(config) {
    this.config = config
  }

  def open(buildlife) {
    def settings = config.get('anthill')
    browse "http://${settings.api_server}:8181/tasks/project/BuildLifeTasks/viewBuildLife?buildLifeId=${buildlife}"
  }

  def request(projectName, workflowName) {
    def request = work { uow ->
      def (project, workflow) = get_workflow_or_complain(projectName, workflowName)
      createRequest(workflow)
    }

    print "Created build request ${request.id} for $workflowName; Waiting for Buildlife..."
    submitRequest(request)

    def thread = Thread.start {
      def complete = false
      while (!complete && !Thread.currentThread().isInterrupted()) {
        sleep  250
        print '.'

        work { uow ->
          request = BuildRequestFactory.getInstance().restore(request.id)
          if (request.status == BuildRequestStatusEnum.BUILD_LIFE_CREATED) {
            def build = request.buildLife
            println "Buildlife ${build.id} is available"
            complete = true
          }
          if (request.status == BuildRequestStatusEnum.BUILD_LIFE_NOT_NEEDED) {
            println "Buildlife is not needed"
            complete = true
          }
          if (request.status == BuildRequestStatusEnum.FAILED) {
            println "Buildlife creation failed"
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
      quit "$workflowName is not an originating workflow"

    def uow = workflow.unitOfWork
    def request = BuildRequest.createOriginatingRequest(
      workflow.buildProfile, uow.user, RequestSourceEnum.MANUAL, uow.user)
    request.forcedFlag = true
    request.unitOfWork = uow
    request.store()
    request
  }
}