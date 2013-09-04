package hilltop.finders

import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.workflow.*

class WorkflowFinder extends Finder {
  private ProjectFinder projectFinder

  def WorkflowFinder(Closure handlers) {
    super(handlers)
    projectFinder = new ProjectFinder(handlers)
  }

  def workflow(projectName, workflowName) {

    def workflow = null
    def project = projectFinder.project(projectName)

    if (project) {
      workflow = WorkflowFactory.getInstance()
        .restoreForProjectAndWorkflowName(project, workflowName)

      if (!workflow)
        error "No such workflow <$workflowName> for project <$project.name>"
    }

    workflow
  }

  def all(project, inactive) {
    def workflows = inactive ?
      WorkflowFactory.getInstance().restoreAllForProject(project) :
      WorkflowFactory.getInstance().restoreAllActiveForProject(project)
    if (inactive)
      workflows = workflows.findAll { w -> !w.isActive }
    workflows
  }
}
