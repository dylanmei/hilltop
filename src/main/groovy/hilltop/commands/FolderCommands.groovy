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

  def show(name) {
    work {
      def folder = finder.one(name)
      echo folder.name

      if (folder.description)
        echo "Description", folder.description

      if (folder.parent)
        echo "Parent-Folder", folder.parent.path

      if (folder.children.size()) {
        echo "Sub-Folders", { line ->
          folder.children.each { f -> line.echo f.name }
        }
      }

      if (folder.projects.size()) {
        echo "Projects", { line ->
          folder.projects.each { p -> line.echo p.name }
        }
      }
    }    
  }
}