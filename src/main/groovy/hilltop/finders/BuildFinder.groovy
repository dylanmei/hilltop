
package hilltop.finders

import com.urbancode.anthill3.domain.buildlife.*

@Mixin(Callbacks)
class BuildFinder {
  def buildlife(id, Closure handler) {
    def build = null
    if (id.isInteger())
      build = BuildLifeFactory.getInstance().restore(id as int)

    if (handler && !build) {
      callback(handler).error("No such buildlife <$id>")
    }

    build
  }
}