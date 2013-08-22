
package hilltop.commands

import hilltop.finders.WorkflowFinder
import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.domain.source.*
import com.urbancode.anthill3.domain.source.plugin.*

@Mixin(ConsoleCommands)
@Mixin(AnthillCommands)
class WorkflowCommands {
  def config

  WorkflowFinder finder = new WorkflowFinder()

  def WorkflowCommands(config) {
    this.config = config
  }

  def show(projectName, workflowName) {
    work {
      def workflow = findWorkflow(projectName, workflowName)
      def project = workflow.project
      def hint = workflow.isOriginating() ? '*' : ''
      echo "${project.name} > ${workflow.name}$hint"

      if (workflow.description)
        echo "Description".padRight(40) + workflow.description

      def definition = workflow.getWorkflowDefinition()
      def lifecycleModel = definition.getLifeCycleModel()
      echo "Lifecycle".padRight(40) + lifecycleModel.name

      if (workflow.isOriginating()) {
        def buildProfile = workflow.buildProfile
        def sourceConfig = buildProfile.sourceConfig
        def sourceConfigType = sourceConfig.sourceConfigType

        if (sourceConfigType.name.endsWith('.plugin.PluginSourceConfig')) {
          def pluginConfig = sourceConfig.asType(PluginSourceConfig)
          def repository = pluginConfig.repositoryArray.first()
          def plugin = repository.plugin
          echo "Source Type".padRight(40) + repository.typeName

          if (plugin.pluginId.endsWith('.plugin.Git')) {
            def repoProps = repository.getPropertyValueGroupsWithType('repo').first().propertyMap
            def sourceProps = pluginConfig.sourcePropertyValueGroups.first().propertyMap

            echo "Repository URL".padRight(40) + repoProps['repoBaseUrl'] + sourceProps['remoteUrl']
            echo "Repository Branch".padRight(40) + sourceProps['branch']

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
          echo "Source Type".padRight(40) + sourceConfigType.name - 'com.urbancode.anthill3.domain.source.'

          if (sourceConfigType.name.endsWith('.git.GitSourceConfig')) {
            echo "Source Type".padRight(40) + sourceConfig.repository.name
            echo "Repository URL".padRight(40) + sourceConfig.repositoryUrl
            echo "Repository Branch".padRight(40) + sourceConfig.revision
          }
          else {
            if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getRepositoryName'))
              echo "Repository Name".padRight(40) + sourceConfig.repositoryName
            if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getWorkspaceName'))
              echo "Workspace Name".padRight(40) + sourceConfig.workspaceName
          }
        }
      }
    }
  }

  def open(projectName, workflowName, admin) {
    def settings = config.get('anthill')
    def url = work {
      def workflow = findWorkflow(projectName, workflowName)
      return admin ?
        "http://${settings.api_server}:8181/tasks/admin/project/workflow/WorkflowTasks/viewWorkflow?workflowId=${workflow.id}" :
        "http://${settings.api_server}:8181/tasks/project/WorkflowTasks/viewDashboard?workflowId=${workflow.id}"
    }

    browse url
  }

  private Workflow findWorkflow(projectName, workflowName) {
    finder.workflow(projectName, workflowName) {
      alert { m -> echo m }
      error { m -> quit m }
    }    
  }
}