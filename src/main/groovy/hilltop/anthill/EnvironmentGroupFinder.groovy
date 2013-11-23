
package hilltop.anthill

import com.urbancode.anthill3.domain.servergroup.*
import com.urbancode.anthill3.domain.environmentgroup.*

@Mixin(FeedbackHelper)
class EnvironmentGroupFinder {
  def EnvironmentGroupFinder(Closure handlers) {
    if (handlers) init_feedback(handlers)
  }

  def one(String name) {
    def group = EnvironmentGroupFactory.getInstance().restoreForName(name)
    if (!group) error "No such environment group <$name>"
    group
  }

  def fetch(ServerGroup environment) {
    return EnvironmentGroupFactory.getInstance().restoreAllForEnvironment(environment)
  }
}
