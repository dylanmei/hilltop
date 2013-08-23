package hilltop.finders

import com.urbancode.anthill3.domain.project.*

@Mixin(FinderCallbacks)
class ProjectFinder {

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

  private String guessProjectName() {
    System.getProperty("user.dir")
      .tokenize(File.separator).last()
  }
}

