
package hilltop.finders

import com.urbancode.anthill3.domain.buildlife.*
import com.urbancode.anthill3.domain.buildrequest.*

class BuildFinder extends Finder {
  def BuildFinder(Closure handlers) {
    super(handlers)
  }

  def buildlife(id) {
    def build = BuildLifeFactory.getInstance().restore(id)

    if (!build) {
      error "No such buildlife <$id>"
    }

    build
  }

  def request(id) {
    def req = BuildRequestFactory.getInstance().restore(id)

    if (!req) {
      error "No such build request <$id>"
    }

    req
  }
}