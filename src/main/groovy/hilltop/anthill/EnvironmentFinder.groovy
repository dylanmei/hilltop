
package hilltop.anthill

import com.urbancode.anthill3.domain.servergroup.*

@Mixin(FeedbackHelper)
class EnvironmentFinder {
  def EnvironmentFinder(Closure handlers) {
    if (handlers) init_feedback(handlers)
  }

  def all() {
    ServerGroupFactory.getInstance().restoreAll()
  }

  def one(name) {
    def environment = ServerGroupFactory.getInstance().restoreForName(name)
    if (!environment) error "No such environment <$name>"
    environment
  }
}
