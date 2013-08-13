
package hilltop
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
      println "Project".padRight(40) + project.name
      if (workflow.getDescription())
        println "Description".padRight(40) + workflow.description

//      def buffer = new StringBuffer()

      def definition = workflow.getWorkflowDefinition()
      def lifecycleModel = definition.getLifeCycleModel()
      println "Lifecycle".padRight(40) + lifecycleModel.name

      if (workflow.isOriginating()) {
        def buildProfile = workflow.getBuildProfile()
        def sourceConfig = buildProfile.getSourceConfig()

        def pluginConfig = sourceConfig.asType(PluginSourceConfig)

        if (pluginConfig) {
          def repository = pluginConfig.repositoryArray.first()
          def plugin = repository.plugin
          println "Source Type".padRight(40) + repository.typeName
          
          if (plugin.pluginId.endsWith('.plugin.Git')) {
            pluginConfig.sourcePropertyValueGroups.each { pvg ->
              for (name in pvg.propertyNames) {
                println "  ${name}:".padRight(20) + pvg.getPropertyValue(name)
//                println String.format('%1$20s:%2$35s', name, pvg.getPropertyValue(name))
              }
            }
          }
          

//          pluginConfig.repositoryArray.each { ra ->
//            println "${ra.typeName}"
            
//            def plugin = ra.getPlugin()
//            println "${plugin.pluginId}"

//            def rd = plugin.getRepositoryDefinition()

            
//            for (rpd in plugin.getRepositoryPropertyDefinitions()) {
//              println "${rpd.name}, ${rpd.label}, ${rpd.description}"
//            }

/*
            ra.propertyValueGroups.each { pvg ->
              println "\t${pvg.name}"
              pvg.propertyNames.each { pn ->
                println "\t\t$pn = ${pvg.getPropertyValue(pn)}"
              }
            }
            */
  //        }
        }
        else {
          println "Source Config\t${definition.getSourceConfigTypeDisplayName()}"
          if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getRepositoryName'))
            println "Repository Name\t${sourceConfig.getRepositoryName()}"
          if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getWorkspaceName'))
            println "Workspace Name\t${sourceConfig.getWorkspaceName()}"
          if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getRevision'))
            println "Repository Rev\t${sourceConfig.getRevision()}"
          if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getRepositoryUrl'))
            println "Repository URL\t${sourceConfig.getRepositoryUrl()}"
        }
      }
    }
  }

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

  def open(projectName, workflowName, admin) {
    def settings = config.get('anthill')
    def url = work {
      def (project, workflow) = get_workflow_or_complain(projectName, workflowName)
      return admin ?
        "http://${settings.api_server}:8181/tasks/admin/project/workflow/WorkflowTasks/viewWorkflow?workflowId=${workflow.id}" :
        "http://${settings.api_server}:8181/tasks/project/WorkflowTasks/viewDashboard?workflowId=${workflow.id}"
    }

    if (url) open_browser(url)
  }
}