
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
      link(buildlife)
    }
    browse url
  }

  def show(id) {
    work {
      def buildlife = buildFinder.buildlife(id as int)
      def project = buildlife.project
      def workflow = buildlife.originatingWorkflow

      echo "$project.name $workflow.name"
      echo link(buildlife)

      def request = buildlife.originatingRequest
      echo "Build Request", request.id

      if (buildlife.statusArray) {
        echo "Status", { line ->
          buildlife.statusArray.each { s -> line.echo "$s.status [${s.dateAssigned.format('d MMM yyyy HH:mm:ss Z')}]" }
        }
      }

      if (buildlife.latestStamp)
        echo "Stamp", buildlife.latestStampValue


      if (buildlife.propertyNames) {
        echo "Properties", { line ->
          buildlife.propertyNames.each { n -> line.echo "$n ${buildlife.getPropertyValue(n)}" }
        }
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

  def link(buildlife) {
    def settings = config.get('anthill')
    "http://${settings.api_server}:8181/tasks/project/BuildLifeTasks/viewBuildLife?buildLifeId=${buildlife.id}"
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