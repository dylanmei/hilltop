
package hilltop.commands

import hilltop.anthill.*
import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.services.build.*
import com.urbancode.anthill3.domain.buildrequest.*
import com.urbancode.anthill3.domain.buildlife.*

class BuildCommands extends AnthillCommands {
  def buildFinder = Finder(BuildFinder)
  def requestFinder = Finder(RequestFinder)
  def workflowFinder = Finder(WorkflowFinder)

  def open(id) {
    def settings = config.get('anthill')
    def buildlife = work {
      buildFinder.one(id as long)
    }
 
    browse link_to(buildlife)
  }

  def show(id) {
    work {
      def buildlife = buildFinder.one(id as long)
      def project = buildlife.project
      def workflow = buildlife.originatingWorkflow

      echo "$project.name $workflow.name"
      echo link_to(buildlife)

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
      def workflow = workflowFinder.one(projectName, workflowName)
      if (!workflow.isOriginating())
        quit "${workflow.name} is not an originating workflow"

      AnthillEngine.create_build_request(workflow)
    }

    print "Created build request ${request.id} for $workflowName; Waiting for Buildlife..."
    AnthillEngine.submit_build_request(request)

    def buildlife = null; while (!buildlife) {
      sleep 250; print '.'

      work {
        request = requestFinder.one(request.id)

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

  def run(id, workflowName, environmentName, openBrowser) {
    def request = work {
      def buildlife = buildFinder.one(id as long)
      def runner = new WorkflowRunner(buildlife, {
        error { m -> quit m }
      })

      runner.request(workflowName, environmentName, [:])
    }

    WorkflowRunner.submit(request)
    echo "Created workflow request $request.id for $request.workflow.name in $request.serverGroup.name"

    if (openBrowser) open(id)
  }
}