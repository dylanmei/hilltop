package hilltop.commands

import hilltop.Config
import hilltop.anthill.FolderFinder
import com.urbancode.anthill3.domain.folder.*

@Mixin(ConsoleHelper)
@Mixin(AnthillHelper)
class FolderCommands {
  def config = new Config()
  def finder = new FolderFinder({
    alert { m -> echo m }; error { m -> quit m }
  })

  def list(inactive) {
    work {
      def folders = finder.all(inactive)
      folders.each { echo it.path }
    }
  }
}