package hilltop.finders

import com.urbancode.anthill3.domain.workflow.*

@Mixin(FinderCallbacks)
class WorkflowFinder {
  
  private ProjectFinder projectFinder = new ProjectFinder()

  def workflow(projectName, workflowName, Closure handler) {

    def workflow = null
    def project = projectFinder.project(projectName, handler)

    if (project) {
      workflow = WorkflowFactory.getInstance()
        .restoreForProjectAndWorkflowName(project, workflowName)

      if (handler) {
        if (!workflow)
          callback(handler).error "No such workflow <$workflowName> for project <$project.name>"
      }
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
