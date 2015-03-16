package hilltop.anthill

import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.workflow.*

@Mixin(FeedbackHelper)
class WorkflowFinder {
  private ProjectFinder projectFinder

  def WorkflowFinder(Closure handlers) {
    if (handlers) init_feedback(handlers)
    projectFinder = new ProjectFinder(handlers)
  }

  def one(projectName, workflowName) {
    def workflow = null
    def project = projectFinder.one(projectName)

    if (project) {
      workflow = WorkflowFactory.getInstance()
        .restoreForProjectAndWorkflowName(project, workflowName)

      if (!workflow)
        error "No such workflow <$workflowName> for project <$project.name>"
    }

    workflow
  }

  def one(workflowId) {
      def workflow = WorkflowFactory.getInstance()
        .restore(Integer.parseInt(workflowId))

      if (!workflow)
        error "No such workflow <$workflowId> found"

      workflow
  }

  def findByName(workflowName) {
      def workflow = WorkflowFactory.getInstance()
        .restoreAllActive()
        .find { w -> w.name == workflowName}

      if (!workflow)
        error "No such workflow <$workflowName> found"

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
