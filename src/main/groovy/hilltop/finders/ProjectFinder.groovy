package hilltop.finders

import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.folder.*

@Mixin(FinderCallbacks)
class ProjectFinder {

  def all(inactive) {
    def projects = inactive ?
      ProjectFactory.getInstance().restoreAll() :
      ProjectFactory.getInstance().restoreAllActive()
    if (inactive)
      projects = projects.findAll { p -> !p.isActive }

    projects
  }

  def project(name, Closure handler) {
    if (name == '.')
      name = guessProjectName()

    def projects = ProjectFactory.getInstance()
      .restoreAllForName(name)

    def project = projects == [] ? null : projects[0]
  
    if (handler) {
      if (!project)
        callback(handler).error("No such project <$name>")
      else if (projects.size() > 1)
        callback(handler).alert("There are ${projects.size()} projects named <$name>; taking the first one")
    }

    project
  }

  def folder(name, Closure handler) {
    def folder = FolderFactory.getInstance().restoreForName(name)
    if (handler && !folder) {
      callback(handler).error "No such folder <$name>"
    }

    folder
  }

  private String guessProjectName() {
    System.getProperty('user.dir')
      .tokenize(File.separator).last()
  }
}

