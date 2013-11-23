package hilltop.commands

import hilltop.anthill.LifecycleFinder
import com.urbancode.anthill3.domain.lifecycle.*

class LifecycleCommands extends AnthillCommands {
  def finder = Finder(LifecycleFinder)

  def show(name) {
    work {
      def lifecycle = finder.one(name)
      echo lifecycle, uri: link_to(lifecycle)

      if (lifecycle.description)
        echo "Description", lifecycle.description
    }
  }

  def open(name) {
    def lifecycle = work {
      finder.one(name)
    }

    browse link_to(lifecycle)
  }

  def list() {
    work {
      def lifecycles = finder.all()
      lifecycles.each {
        echo it.name, it.description ?: ''
      }
    }
  }
}
