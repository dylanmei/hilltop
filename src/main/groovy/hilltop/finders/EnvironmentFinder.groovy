
package hilltop.finders

import com.urbancode.anthill3.domain.servergroup.*

@Mixin(Callbacks)
class EnvironmentFinder {
  def all() {
    ServerGroupFactory.getInstance().restoreAll()
  }

  def environment(name, Closure handler) {
    def env = ServerGroupFactory.getInstance().restoreForName(name)

    if (handler && !env) {
      callback(handler).error("No such environment <$name>")
    }
    env
  }
}
