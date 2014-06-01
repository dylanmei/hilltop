package hilltop.commands

import hilltop.anthill.FolderFinder
import com.urbancode.anthill3.domain.folder.*

class FolderCommands extends AnthillCommands {
  def FolderCommands(out) {
    super(out)
  }

  def list(inactive) {
    send work {
      def folders = finder(FolderFinder).all(inactive)

      return folders.collect {[
        id: it.id,
        name: it.path,
        description: it.description,
      ]}
    }
  }

  def show(name) {
    send work {
      def folder = finder(FolderFinder).one(name)

      return [
        id: folder.id,
        name: folder.name,
        description: folder.description,
        parent_folder: folder.parent?.path,
        sub_folders: folder.children.collect {[
          id: it.id, name: it.name,
        ]},
        projects: folder.projects.collect {[
          id: it.id, name: it.name,
        ]},
      ]
    }
  }
}
