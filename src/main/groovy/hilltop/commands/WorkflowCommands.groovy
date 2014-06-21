
package hilltop.commands

import hilltop.anthill.*
import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.domain.source.*
import com.urbancode.anthill3.domain.source.plugin.*
import com.urbancode.anthill3.domain.buildlife.*

class WorkflowCommands extends AnthillCommands {
  def WorkflowCommands(out) {
    super(out)
  }

  def show(projectName, workflowName) {
    send work {
      def workflow = finder(WorkflowFinder).one(projectName, workflowName)
      def project = workflow.project
      def definition = workflow.workflowDefinition

      def result = [
        id: workflow.id,
        name: workflow.name,
        url: link_to(workflow),
        project: project.name,
        description: workflow.description,
        originating: workflow.isOriginating(),
        lifecycle: definition.lifeCycleModel?.name,
      ]

      if (!workflow.isOriginating()) {
        result['environments'] = workflow.serverGroupArray.collect { it.name }
      }
      else {
        def buildProfile = workflow.buildProfile
        def sourceConfig = buildProfile.sourceConfig
        def sourceConfigType = sourceConfig.sourceConfigType

        if (sourceConfigType.name.endsWith('.plugin.PluginSourceConfig')) {
          def pluginConfig = sourceConfig.asType(PluginSourceConfig)
          def repository = pluginConfig.repositoryArray.first()
          def plugin = repository.plugin
          result['source_type'] = repository.typeName

          if (plugin.pluginId.endsWith('.plugin.Git')) {
            def repoProps = repository.getPropertyValueGroupsWithType('repo').first().propertyMap
            def sourceProps = pluginConfig.sourcePropertyValueGroups.first().propertyMap

            result['repository_url'] = repoProps['repoBaseUrl'].toString() + sourceProps['remoteUrl'].toString()
            result['repository_branch'] = sourceProps['branch'].toString()
          }
        }
        else {
          if (sourceConfigType.name.endsWith('.git.GitSourceConfig')) {
            result['source_type'] = sourceConfig.repository.name
            result['repository_url'] = sourceConfig.repositoryUrl
            result['repository_branch'] = sourceConfig.revision
          }
          else {
            result['source_type'] = sourceConfigType.name - 'com.urbancode.anthill3.domain.source.'
            if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getRepositoryName'))
              result['repository_name'] = sourceConfig.repositoryName
            if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getWorkspaceName'))
              result['workspace_name'] = sourceConfig.workspaceName
          }
        }
      }

      println 'returning result!'
      return result
    }
  }

  def open(projectName, workflowName, admin) {
    def workflow = work {
      finder(WorkflowFinder).one(projectName, workflowName)
    }

    browse link_to {
      resource workflow
      attributes admin: admin
    }
  }

  def list(projectName, inactive) {
    send work {
      def project = finder(ProjectFinder).one(projectName)
      def workflows = finder(WorkflowFinder).all(project, inactive).sort { a, b ->
        if (a.isOriginating()) return b.isOriginating() ? 0 : -1
        if (b.isOriginating()) return a.isOriginating() ? 0 :  1
        a.getName() <=> b.getName()
      }

      workflows.collect {[
        id: it.id,
        name: it.name,
        url: link_to(it),
        description: it.description,
        originating: it.isOriginating(),
      ]}
    }
  }

  def remove(projectName, workflowName, force, noop) {
    def workflow, project
    work {
      workflow = finder(WorkflowFinder).one(projectName, workflowName)
      project = workflow.project
    }

    def destroyer = new WorkflowDestroyer(connect(), {
      error { m -> quit m }
      alert { m -> println m }
    })

    destroyer.go(workflow, force, noop)
    println "Workflow <$workflow.name> has been removed from Project <$workflow.project.name>"
  }
}
