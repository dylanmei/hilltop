
package hilltop.commands

import hilltop.Config
import hilltop.finders.ProjectFinder
import hilltop.finders.WorkflowFinder
import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.domain.source.*
import com.urbancode.anthill3.domain.source.plugin.*

@Mixin(ConsoleHelper)
@Mixin(AnthillHelper)
class WorkflowCommands {
  def config = new Config()
  def projectFinder = new ProjectFinder({
    alert { m -> echo m }; error { m -> quit m }
  })
  def workflowFinder = new WorkflowFinder({
    alert { m -> echo m }; error { m -> quit m }
  })

  def show(projectName, workflowName) {
    work {
      def workflow = workflowFinder.workflow(projectName, workflowName)
      def project = workflow.project

      echo workflow.name
      echo "Project", project.name

      echo "Originating", workflow.isOriginating() ? "Yes" : "No"

      if (workflow.description)
        echo "Description", workflow.description

      def definition = workflow.getWorkflowDefinition()
      def lifecycleModel = definition.getLifeCycleModel()
      echo "Lifecycle", lifecycleModel.name

      if (workflow.isOriginating()) {
        def buildProfile = workflow.buildProfile
        def sourceConfig = buildProfile.sourceConfig
        def sourceConfigType = sourceConfig.sourceConfigType

        if (sourceConfigType.name.endsWith('.plugin.PluginSourceConfig')) {
          def pluginConfig = sourceConfig.asType(PluginSourceConfig)
          def repository = pluginConfig.repositoryArray.first()
          def plugin = repository.plugin
          echo "Source Type", repository.typeName

          if (plugin.pluginId.endsWith('.plugin.Git')) {
            def repoProps = repository.getPropertyValueGroupsWithType('repo').first().propertyMap
            def sourceProps = pluginConfig.sourcePropertyValueGroups.first().propertyMap

            echo "Repository URL", repoProps['repoBaseUrl'].toString() + sourceProps['remoteUrl'].toString()
            echo "Repository Branch", sourceProps['branch']

//            repository.propertyValueGroups.each { pvg ->
//              for (name in pvg.propertyNames) {
//                echo "  ${name}:".padRight(20) + pvg.getDisplayedValue(name)
//              }
//            }

//            pluginConfig.sourcePropertyValueGroups.each { pvg ->
//              for (name in pvg.propertyNames) {
//                echo "  ${name}:".padRight(20) + pvg.getPropertyValue(name)
//              }
//            }
          }
        }
        else {
          echo "Source Type", sourceConfigType.name - 'com.urbancode.anthill3.domain.source.'

          if (sourceConfigType.name.endsWith('.git.GitSourceConfig')) {
            echo "Source Type", sourceConfig.repository.name
            echo "Repository URL", sourceConfig.repositoryUrl
            echo "Repository Branch", sourceConfig.revision
          }
          else {
            if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getRepositoryName'))
              echo "Repository Name", sourceConfig.repositoryName
            if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getWorkspaceName'))
              echo "Workspace Name", sourceConfig.workspaceName
          }
        }
      }
    }
  }

  def open(projectName, workflowName, admin) {
    def settings = config.get('anthill')
    def url = work {
      def workflow = workflowFinder.workflow(projectName, workflowName)
      return admin ?
        "http://${settings.api_server}:8181/tasks/admin/project/workflow/WorkflowTasks/viewWorkflow?workflowId=${workflow.id}" :
        "http://${settings.api_server}:8181/tasks/project/WorkflowTasks/viewDashboard?workflowId=${workflow.id}"
    }

    browse url
  }

  def list(projectName, inactive) {
    work {
      def project = projectFinder.project(projectName)
      def workflows = workflowFinder.all(project, inactive).sort { a, b ->
        if (a.isOriginating()) return b.isOriginating() ? 0 : -1
        if (b.isOriginating()) return a.isOriginating() ? 0 :  1
        a.getName() <=> b.getName()
      }

      workflows.each { w -> echo "${w.isOriginating() ? '*' : ' '} ${w.name}" }
    }
  }
}