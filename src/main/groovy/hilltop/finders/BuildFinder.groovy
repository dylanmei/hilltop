
package hilltop.finders

import com.urbancode.anthill3.domain.buildlife.*
import com.urbancode.anthill3.domain.buildrequest.*

@Mixin(FinderCallbacks)
class BuildFinder {
  def buildlife(id, Closure handler) {
    def build = BuildLifeFactory.getInstance().restore(id)

    if (handler && !build) {
      callback(handler).error("No such buildlife <$id>")
    }

    build
  }

  def request(id, Closure handler) {
    def req = BuildRequestFactory.getInstance().restore(id)

    if (handler && !req) {
      callback(handler).error("No such build request <$id>")
    }

    req
  }
}