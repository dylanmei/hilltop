
package hilltop.anthill

import com.urbancode.anthill3.domain.buildrequest.*

@Mixin(FeedbackHelper)
class RequestFinder {
  def RequestFinder(Closure handlers) {
    if (handlers) init_feedback(handlers)
  }

  def one(id) {
    def req = BuildRequestFactory.getInstance().restore(id)
    if (!req) error "No such build request <$id>"
    req
  }
}