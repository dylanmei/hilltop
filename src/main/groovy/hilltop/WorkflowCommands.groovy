
package hilltop
import com.urbancode.anthill3.domain.project.*;
import com.urbancode.anthill3.domain.source.*;
import com.urbancode.anthill3.domain.workflow.*;

class WorkflowCommands extends AnthillCommands {
  def config

  def WorkflowCommands(config) {
    this.config = config
  }

  def show(projectName, workflowName) {
    work {
      def (project, workflow) = get_workflow_or_complain(projectName, workflowName)

      def hint = workflow.isOriginating() ? '*' : ''
      println "${project.getName()}-${workflow.getName()}$hint"


      if (workflow.getDescription())
        println "Description\t${project.getDescription()}"

      def definition = workflow.getWorkflowDefinition()
      def lifecycleModel = definition.getLifeCycleModel()
      println "Lifecycle\t${lifecycleModel.getName()}"

      def buildProfile = workflow.getBuildProfile()
      def sourceConfig = buildProfile.getSourceConfig()

      println "Source Config\t${definition.getSourceConfigTypeDisplayName()}"
      if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getRepositoryName'))
        println "Repository Name\t${sourceConfig.getRepositoryName()}"
      if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getWorkspaceName'))
        println "Workspace Name\t${sourceConfig.getWorkspaceName()}"
      if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getRevision'))
        println "Repository Rev\t${sourceConfig.  getRevision()}"
      if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getRepositoryUrl'))
        println "Repository URL\t${sourceConfig.getRepositoryUrl()}"
    }
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