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

    def hints = name.split('/')
    if (hints.size() > 1) {
      name = hints[-1]
      hints = hints[0..<hints.size()-1].collect { it.toLowerCase() }
    }
    else {
      hints = null
    }

    def projects = ProjectFactory.getInstance()
      .restoreAllForName(name)

    if (hints?.size() > 0) {
      projects = projects.findAll { p ->
        def paths = p.folder.path.split('/')
          .findAll { it != '' }
          .collect { it.toLowerCase() }
        paths.intersect(hints).size() == hints.size()
      }
    }

    if (!projects) {
      error "No such project <$name>"
    }
    else if (projects.size() == 1) {
      projects[0]
    }
    else  {
      alert "There are ${projects.size()} projects named <$name>"
      projects.findAll { p -> p.isActive() }.each {
        alert " * ${it.id}: ${it.folder.path}"
      }
      error 'Use the project id or include part of the path to disambiguate'
    }
  }

  private String guessProjectName() {
    System.getProperty('user.dir')
      .tokenize(File.separator).last()
  }
}

