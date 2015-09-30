
package hilltop.commands

import hilltop.anthill.*
import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.services.build.*
import com.urbancode.anthill3.domain.buildrequest.*
import com.urbancode.anthill3.domain.buildlife.*

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
      map(buildlife)
    }
  }

  def latest(projectName, workflowName, statusName) {
    send work {
      def workflow = finder(WorkflowFinder).one(projectName, workflowName)
      def project = workflow.project
      def status = statusName ? finder(StatusFinder).one(workflow, statusName) : null
      def buildlife = finder(BuildFinder).latest(workflow, status)
      map(buildlife)
    }
  }

  def remove(id) {
    work {
      def buildlife = finder(BuildFinder).one(id as long)
      buildlife.delete()
    }
  }

  // NOTE: usually there's a 1-1 mapping with the command name,
  // but since 'new' is a language keyword, this method is 'start'
  def start(projectName, workflowName, openBrowser, waitForCompletion, propertiesPath, properties) {

    def request = work {
      def workflow = finder(WorkflowFinder).one(projectName, workflowName)
      if (!workflow.isOriginating())
        quit "${workflow.name} is not an originating workflow"

      def propertyMap = PropertyHelper.fromArguments(properties)
      if (propertiesPath) {
        propertyMap += PropertyHelper.fromFile(propertiesPath)
      }

      AnthillEngine.create_build_request(workflow, propertyMap)
    }

    work {
      AnthillEngine.submit_build_request(request)
      statusln("Created build request ${request.id} for $workflowName")
      status("Waiting for Buildlife...")
    }

    def buildlife = null; while (!buildlife) {
      sleep 250; statusTick()

      work {
        request = finder(RequestFinder).one(request.id)

        if (request.status == BuildRequestStatusEnum.BUILD_LIFE_CREATED) {
          buildlife = request.buildLife
          statusln("\nBuildlife ${buildlife.id} is available")
        }
      }
      if (request.status == BuildRequestStatusEnum.BUILD_LIFE_NOT_NEEDED) {
        println "Buildlife is not needed"; break
      }
      if (request.status == BuildRequestStatusEnum.FAILED) {
        println "Buildlife creation failed"; break
      }
    }

    if (buildlife) {
      if (openBrowser)
        open(buildlife.id)
      else
        if (waitForCompletion)
        {
          status("Waiting for build to complete ")

          def result = null
          while (!result) {
            sleep 5000
            statusTick()
            work {
              buildlife = finder(BuildFinder).one(buildlife.id)

              if (buildlife.statusArray && buildlife.statusArray.length > 0) {
                result = buildlife.statusArray[0].status.name
              }
            }
          }

          statusln(" $result!")
        }

        out.send(
        [
          id: buildlife.id
        ])
    }
  }

  def run(id, workflowName, environmentName, openBrowser, waitForCompletion, properties) {
    def request = work {
      def buildlife = finder(BuildFinder).one(id as long)
      def runner = new WorkflowRunner(buildlife, {
        error { m -> quit m }
      })

      def propertyMap = PropertyHelper.fromArguments(properties)

      runner.request(workflowName, environmentName, propertyMap)
    }

    work {
      WorkflowRunner.submit(request)
      println "Created workflow request $request.id for $request.workflow.name in $request.serverGroup.name"
    }

    if (waitForCompletion) {
      status("Waiting ")

      def result = null
      while (!result) {
        sleep 5000
        statusTick()
        work {
          request = finder(RequestFinder).one(request.id)

          if (request.status == BuildRequestStatusEnum.STARTED_WORKFLOW) {
            def workflowCaseStatus = request.workflowCase.status
            if (workflowCaseStatus.isDone()) {
              result = workflowCaseStatus
            }
          }
        }
      }

      statusln(" $result!")
    }

    if (openBrowser) open(id)
  }

  def map(buildlife) {
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
        statuses = buildlife.statusArray.collect {
          [key: it.status, value: it.dateAssigned.format('d MMM yyyy HH:mm:ss Z')]
        }
      }
    }
    [
      id: buildlife.id,
      name: "Buildlife $buildlife.id",
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