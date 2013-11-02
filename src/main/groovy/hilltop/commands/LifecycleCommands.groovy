package hilltop.commands

import hilltop.Config
import hilltop.anthill.LifecycleFinder
import com.urbancode.anthill3.domain.lifecycle.*

@Mixin(ConsoleHelper)
@Mixin(AnthillHelper)
class LifecycleCommands {
  def config = new Config()
  def finder = new LifecycleFinder({
    error { m -> quit m }
  })

  def show(name) {
    work {
      def lifecycle = finder.lifecycle(name)

      echo lifecycle.name
      echo link_to(lifecycle)

      if (lifecycle.description)
        echo "Description", lifecycle.description
    }
  }

  def open(name) {
    def settings = config.get('anthill')
    def lifecycle = work {
      finder.lifecycle(name)
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
