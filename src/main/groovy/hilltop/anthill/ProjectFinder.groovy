package hilltop.anthill

import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.folder.*

@Mixin(FeedbackHelper)
class ProjectFinder {
  def ProjectFinder(Closure handlers) {
    if (handlers) init_feedback(handlers)
  }

  def all(inactive) {
    def projects = inactive ?
      ProjectFactory.getInstance().restoreAll() :
      ProjectFactory.getInstance().restoreAllActive()
    if (inactive)
      projects = projects.findAll { p -> !p.isActive }

    projects
  }

  def one(name) {

    if (name.isLong())
    {
      def project = ProjectFactory.getInstance().restore(name as long)
      if (!project) { error "No such project <$name>" }
      return project
    }
    
    if (name == '.')
      name = guessProjectName()

    def projects = ProjectFactory.getInstance()
      .restoreAllForName(name)

    if (!projects) {
      error "No such project <$name>"
    }
    else if (projects.size() == 1) {
      projects[0]
    }
    else  {
      alert "There are ${projects.size()} projects named <$name>; taking the first one"
      projects.sort { it.isActive() ? -1 : 1 }.first()
    }
  }

  private String guessProjectName() {
    System.getProperty('user.dir')
      .tokenize(File.separator).last()
  }
}

