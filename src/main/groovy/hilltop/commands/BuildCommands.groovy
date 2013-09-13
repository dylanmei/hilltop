
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
  def workflowFinder = new WorkflowFinder({
    error { m -> quit m }
  })
  def buildFinder = new BuildFinder({
    error { m -> quit m }
  })

  def open(id) {
    def settings = config.get('anthill')
    def url = work {
      def buildlife = buildFinder.buildlife(id as int)
      "http://${settings.api_server}:8181/tasks/project/BuildLifeTasks/viewBuildLife?buildLifeId=${buildlife.id}"
    }
    browse url
  }

  def show_request(id) {
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
        request.propertyNames.each { n -> line.echo "$n ${request.getPropertyValue(n)}" }
      }      
    }
  }

  def start(projectName, workflowName, openBrowser) {
    def request = work {
      def workflow = workflowFinder.workflow(projectName, workflowName)
      createRequest(workflow)
    }

    print "Created build request ${request.id} for $workflowName; Waiting for Buildlife..."
    submitRequest(request)

    def buildlife = null; while (!buildlife) {
      sleep 250; print '.'

      work {
        request = buildFinder.request(request.id)

        if (request.status == BuildRequestStatusEnum.BUILD_LIFE_CREATED) {
          buildlife = request.buildLife
          echo "Buildlife ${buildlife.id} is available"
        }
      }
      if (request.status == BuildRequestStatusEnum.BUILD_LIFE_NOT_NEEDED) {
        echo "Buildlife is not needed"; break
      }
      if (request.status == BuildRequestStatusEnum.FAILED) {
        echo "Buildlife creation failed"; break
      }
    }

    if (buildlife && openBrowser)
      open(buildlife.id)
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
}