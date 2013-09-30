
package hilltop.anthill

import com.urbancode.anthill3.domain.buildlife.*
import com.urbancode.anthill3.domain.buildrequest.*

@Mixin(FeedbackHelper)
class BuildFinder {
  def BuildFinder(Closure handlers) {
    if (handlers) init_feedback(handlers)
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