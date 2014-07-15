
package hilltop.anthill

import com.urbancode.anthill3.domain.agent.*

@Mixin(FeedbackHelper)
class AgentFinder {
  def AgentFinder(Closure handlers) {
    if (handlers) init_feedback(handlers)
  }

  def all() {
    AgentFactory.getInstance().restoreAll()
  }

  def one(name) {
    def factory = AgentFactory.getInstance()
    def agent = factory.restoreForName(name)
    if (!agent) {
      agent = factory.restoreAll().find {
        it.name.toLowerCase() == name.toLowerCase()
      }
    }
    if (!agent) error "No such agent <$name>"
    agent
  }
}
