
package hilltop.anthill

import com.urbancode.anthill3.domain.servergroup.*
import com.urbancode.anthill3.domain.environmentgroup.*

@Mixin(FeedbackHelper)
class EnvironmentFinder {
  def EnvironmentFinder(Closure handlers) {
    if (handlers) init_feedback(handlers)
  }

  def all() {
    ServerGroupFactory.getInstance().restoreAll()
  }

  def environment(name) {
    def env = ServerGroupFactory.getInstance()
      .restoreForName(name)
    if (!env)
      error "No such environment <$name>"
    env
  }

  def group(name) {
    def group = EnvironmentGroupFactory.getInstance()
      .restoreForName(name)
    if (!group)
      error "No such environment group <$name>"
    group
  }
}
