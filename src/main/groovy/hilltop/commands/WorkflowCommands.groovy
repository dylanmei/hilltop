
package hilltop.commands
import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.source.*
import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.domain.source.plugin.*

class WorkflowCommands extends AnthillCommands {
  def config

  def WorkflowCommands(config) {
    this.config = config
  }

  def show(projectName, workflowName) {
    work {
      def (project, workflow) = get_workflow_or_complain(projectName, workflowName)

      def hint = workflow.isOriginating() ? '*' : ''
      println "${workflow.getName()}$hint"
//      println "Project".padRight(40) + project.name
      if (workflow.getDescription())
        println "Description".padRight(40) + workflow.description

      def definition = workflow.getWorkflowDefinition()
      def lifecycleModel = definition.getLifeCycleModel()
      println "Lifecycle".padRight(40) + lifecycleModel.name

      if (workflow.isOriginating()) {
        def buildProfile = workflow.buildProfile
        def sourceConfig = buildProfile.sourceConfig
        def sourceConfigType = sourceConfig.sourceConfigType

        if (sourceConfigType.name.endsWith('.plugin.PluginSourceConfig')) {
          def pluginConfig = sourceConfig.asType(PluginSourceConfig)
          def repository = pluginConfig.repositoryArray.first()
          def plugin = repository.plugin
          println "Source Type".padRight(40) + repository.typeName

          if (plugin.pluginId.endsWith('.plugin.Git')) {
            def repoProps = repository.getPropertyValueGroupsWithType('repo').first().propertyMap
            def sourceProps = pluginConfig.sourcePropertyValueGroups.first().propertyMap

            println "Repository URL".padRight(40) + repoProps['repoBaseUrl'] + sourceProps['remoteUrl']
            println "Repository Branch".padRight(40) + sourceProps['branch']

//            repository.propertyValueGroups.each { pvg ->
//              for (name in pvg.propertyNames) {
//                println "  ${name}:".padRight(20) + pvg.getDisplayedValue(name)
//              }
//            }

//            pluginConfig.sourcePropertyValueGroups.each { pvg ->
//              for (name in pvg.propertyNames) {
//                println "  ${name}:".padRight(20) + pvg.getPropertyValue(name)
//              }
//            }
          }
        }
        else if (sourceConfigType.name.endsWith('.git.GitSourceConfig')) {
          println "Source Type".padRight(40) + sourceConfig.repository.name
          println "Repository URL".padRight(40) + sourceConfig.repositoryUrl
          println "Repository Branch".padRight(40) + sourceConfig.revision
        }
        else {
          println "Source Type".padRight(40) + sourceConfigType.name
          if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getRepositoryName'))
            println "Repository Name".padRight(40) + sourceConfig.repositoryName
          if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getWorkspaceName'))
            println "Workspace Name".padRight(40) + sourceConfig.workspaceName
        }
      }
    }
  }

/*
  def create(projectName, workflowName, originating) {
    work { uow ->
      def project = get_project_or_complain(projectName)
      def workflow = new Workflow(project)
      workflow.name = workflowName
//      if (originating) {
//        def buildProfile = new BuildProfile(project, workflowName)
//      }
      workflow.setUnitOfWork(uow)
      workflow.store()
    }
    println "Workflow <${workflowName}> has been created"
  }

  def remove(projectName, workflowName) {
    work {
      def (project, workflow) = get_workflow_or_complain(projectName, workflowName)
      workflow.delete()
    }

    println "Workflow <${workflowName}> has been removed"
  }
*/

  def open(projectName, workflowName, admin) {
    def settings = config.get('anthill')
    def url = work {
      def (project, workflow) = get_workflow_or_complain(projectName, workflowName)
      return admin ?
        "http://${settings.api_server}:8181/tasks/admin/project/workflow/WorkflowTasks/viewWorkflow?workflowId=${workflow.id}" :
        "http://${settings.api_server}:8181/tasks/project/WorkflowTasks/viewDashboard?workflowId=${workflow.id}"
    }

    browse url
  }
}