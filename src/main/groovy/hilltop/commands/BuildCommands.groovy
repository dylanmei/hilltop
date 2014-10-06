
package hilltop.commands

import hilltop.anthill.*
import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.services.build.*
import com.urbancode.anthill3.domain.buildrequest.*
import com.urbancode.anthill3.domain.buildlife.*
import com.urbancode.anthill3.runtime.scripting.helpers.*
import com.urbancode.devilfish.server.*
import com.urbancode.devilfish.services.*
import com.urbancode.devilfish.services.command.*
import java.io.*

class BuildCommands extends AnthillCommands {
  def BuildCommands(out) {
    super(out)
  }

  def open(id) {
    def buildlife = work {
      finder(BuildFinder).one(id as long)
    }

    browse link_to(buildlife)
  }

  def show(id) {
    send work {
      def buildlife = finder(BuildFinder).one(id as long)
      def project = buildlife.project
      def workflow = buildlife.originatingWorkflow

      def propertyNames = []
      if (buildlife.propertyNames) {
        propertyNames = buildlife.propertyNames
      }

      def request, statuses = []
      if (!buildlife.isArchived()) {
        request = buildlife.originatingRequest
        if (buildlife.statusArray) {
          statuses = buildlife.statusArray.collect {[
            time: it.dateAssigned.format('d MMM yyyy HH:mm:ss Z'),
            status: it.status,
            origin: getOriginHash(it.origin)]
          }
        }
      }

      [
        id: buildlife.id,
        name: "Buildlife $id",
        url: link_to(buildlife),
        project: project.name,
        workflow: workflow.name,
        preflight: buildlife.isPreflight(),
        inactive: buildlife.isInactive(),
        archived: buildlife.isArchived(),
        stamp: buildlife.latestStampValue,
        build_request: request?.id,
        build_status: statuses,
        properties: propertyNames.collect {
          [key: it, value: buildlife.getPropertyValue(it)]
        },
      ]
    }
  }

  def remove(id) {
    work {
      def buildlife = finder(BuildFinder).one(id as long)
      buildlife.delete()
    }
  }

  def start(projectName, workflowName, openBrowser) {
    def request = work {
      def workflow = finder(WorkflowFinder).one(projectName, workflowName)
      if (!workflow.isOriginating())
        quit "${workflow.name} is not an originating workflow"

      AnthillEngine.create_build_request(workflow)
    }

    work {
      AnthillEngine.submit_build_request(request)
      print "Created build request ${request.id} for $workflowName; Waiting for Buildlife..."
    }

    def buildlife = null; while (!buildlife) {
      sleep 250; print '.'

      work {
        request = finder(RequestFinder).one(request.id)

        if (request.status == BuildRequestStatusEnum.BUILD_LIFE_CREATED) {
          buildlife = request.buildLife
          println "Buildlife ${buildlife.id} is available"
        }
      }
      if (request.status == BuildRequestStatusEnum.BUILD_LIFE_NOT_NEEDED) {
        println "Buildlife is not needed"; break
      }
      if (request.status == BuildRequestStatusEnum.FAILED) {
        println "Buildlife creation failed"; break
      }
    }

    if (buildlife && openBrowser)
      open(buildlife.id)
  }

  def run(id, workflowName, environmentName, openBrowser) {
    def request = work {
      def buildlife = finder(BuildFinder).one(id as long)
      def runner = new WorkflowRunner(buildlife, {
        error { m -> quit m }
      })

      runner.request(workflowName, environmentName, [:])
    }

    work {
      WorkflowRunner.submit(request)
      println "Created workflow request $request.id for $request.workflow.name in $request.serverGroup.name"
    }

    if (openBrowser) open(id)
  }

  def getOriginHash(origin)
  {
    if (origin instanceof BuildLifeStatusOriginJobTrace) {
      def trace = origin.jobTrace
      return [
         trace: "$trace.name",
         status: "$trace.status",
         steps: trace.stepTraceArray.collect 
         {[
          name: it.name,
          commands: it.commandTraceArray.collect 
          {[
            name: it.name
      //      status: it.status
          ]}
        ]}
      ]
    }

/* this was ported from scripting API, but it doesn't work because the serviceFactory is NULL
      def agentEndpoint = trace.getAgent().getEndpoint()
      println "Getting log file from <$agentEndpoint>"

      trace.stepTraceArray.each {
        it.commandTraceArray.each {

          def serviceRegistry = ServiceRegistry.getInstance()
          def serviceFactory = serviceRegistry.getService(CommandServiceClientFactory.SERVICE_NAME)
          def client = serviceFactory.newCommandServiceClient(agentEndpoint)
          
          def receipt = new CommandReceipt(it.commandHandle())
          Long.valueOf(client.getILogFileLineCount(receipt, File.createTempFile("temp", Long.toString(System.nanoTime()))))
        }    
      }*/
  }
}
