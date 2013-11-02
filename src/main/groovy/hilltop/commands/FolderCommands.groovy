package hilltop.commands

import hilltop.anthill.FolderFinder
import com.urbancode.anthill3.domain.folder.*

class FolderCommands extends AnthillCommands {
  def finder = Finder(FolderFinder)

  def list(inactive) {
    work {
      def folders = finder.all(inactive)
      folders.each { echo it.path }
    }
  }
}