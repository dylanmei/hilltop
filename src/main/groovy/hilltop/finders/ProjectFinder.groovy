package hilltop.finders

import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.folder.*

class ProjectFinder extends Finder {
  def ProjectFinder(Closure handlers) {
    super(handlers)
  }

  def all(inactive) {
    def projects = inactive ?
      ProjectFactory.getInstance().restoreAll() :
      ProjectFactory.getInstance().restoreAllActive()
    if (inactive)
      projects = projects.findAll { p -> !p.isActive }

    projects
  }

  def project(name) {
    if (name == '.')
      name = guessProjectName()

    def projects = ProjectFactory.getInstance()
      .restoreAllForName(name)

    def project = projects == [] ? null : projects[0]

    if (!project)
      error "No such project <$name>"
    else if (projects.size() > 1)
      alert "There are ${projects.size()} projects named <$name>; taking the first one"

    project
  }

  def folder(name) {
    def folder = FolderFactory.getInstance().restoreForName(name)
    if (!folder) {
      error "No such folder <$name>"
    }

    folder
  }

  private String guessProjectName() {
    System.getProperty('user.dir')
      .tokenize(File.separator).last()
  }
}

