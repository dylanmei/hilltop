
package hilltop.anthill

import com.urbancode.anthill3.domain.lifecycle.*

@Mixin(FeedbackHelper)
class LifecycleFinder {
  def LifecycleFinder(Closure handlers) {
    if (handlers) init_feedback(handlers)
  }

  def all() {
    LifeCycleModelFactory.getInstance().restoreAll()
  }

  def lifecycle(name) {
    def lifecycle = LifeCycleModelFactory.getInstance()
      .restoreForName(name)
    if (!lifecycle)
      error "No such lifecycle <$name>"
    lifecycle
  }
}
