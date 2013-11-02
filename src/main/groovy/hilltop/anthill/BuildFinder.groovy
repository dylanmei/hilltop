
package hilltop.anthill

import com.urbancode.anthill3.domain.buildlife.*

@Mixin(FeedbackHelper)
class BuildFinder {
  def BuildFinder(Closure handlers) {
    if (handlers) init_feedback(handlers)
  }

  def one(id) {
    def build = BuildLifeFactory.getInstance().restore(id)
    if (!build) error "No such buildlife <$id>"
    build
  }
}