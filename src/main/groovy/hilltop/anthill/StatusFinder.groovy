
package hilltop.anthill

import com.urbancode.anthill3.domain.status.*

@Mixin(FeedbackHelper)
class StatusFinder {
  def StatusFinder(Closure handlers) {
    if (handlers) init_feedback(handlers)
  }

  def one(name, lifeCycleModel) {
     def groups = StatusGroupFactory.getInstance().restoreAll()
     def group = groups.find { it.lifeCycleModel == lifeCycleModel }

     return group.getStatus(name)
  }
}