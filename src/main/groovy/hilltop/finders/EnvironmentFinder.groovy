
package hilltop.finders

import com.urbancode.anthill3.domain.servergroup.*

class EnvironmentFinder extends Finder {
  def EnvironmentFinder(Closure handlers) {
    super(handlers)
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
