
package hilltop.anthill

import com.urbancode.anthill3.domain.status.*

@Mixin(FeedbackHelper)
class StatusFinder {
  def StatusFinder(Closure handlers) {
    if (handlers) init_feedback(handlers)
  }

  def one(workflow, statusName) {
     def groups = StatusGroupFactory.getInstance().restoreAll()
     def group = groups.find { it.lifeCycleModel == workflow.workflowDefinition.lifeCycleModel }

     group.getStatus(statusName)
  }
}