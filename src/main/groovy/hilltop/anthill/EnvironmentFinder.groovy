
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

  def environment(name) {
    def env = ServerGroupFactory.getInstance()
      .restoreForName(name)

    if (!env) {
      error "No such environment <$name>"
    }

    env
  }
}
