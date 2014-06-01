package hilltop.commands

import hilltop.anthill.LifecycleFinder
import com.urbancode.anthill3.domain.lifecycle.*

class LifecycleCommands extends AnthillCommands {
  def LifecycleCommands(out) {
    super(out)
  }

  def show(name) {
    send work {
      def lifecycle = finder(LifecycleFinder).one(name)

      [
        id: lifecycle.id,
        name: lifecycle.name,
        url: link_to(lifecycle),
        description: lifecycle.description,
      ]
    }
  }

  def open(name) {
    def lifecycle = work {
      finder(LifecycleFinder).one(name)
    }

    browse link_to(lifecycle)
  }

  def list() {
    send work {
      def lifecycles = finder(LifecycleFinder).all()

      lifecycles.collect {[
        id: it.id,
        name: it.name,
        url: link_to(it),
        description: it.description,
      ]}
    }
  }
}
