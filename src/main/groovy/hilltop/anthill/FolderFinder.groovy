package hilltop.anthill

import com.urbancode.anthill3.domain.folder.*

@Mixin(FeedbackHelper)
class FolderFinder {
  def FolderFinder(Closure handlers) {
    if (handlers) init_feedback(handlers)
  }

  def all(inactive) {
    inactive ?
      FolderFactory.getInstance().restoreAll() :
      FolderFactory.getInstance().restoreAllActive()
  }

  def one(name) {
    def folder = null

    if (name == '') name = '/'
    if (name == '/') {
      folder = FolderFactory.getInstance().restoreRoot()
    }
    else if (name[0] == '/') {
      if (name ==~ '^/.+[^/]$') {
        // if name begins with a slash but does not
        // end with a slash, coerce it into path format.
        name += '/'
      }

      def steps = name.split('/').collect { it.toLowerCase() }
      def step = FolderFactory.getInstance().restoreRoot()
      for (int i = 1; i < steps.size(); i++) {
        step = step.children.find { it.name.toLowerCase() == steps[i] }
        if (i + 1 == steps.size()) folder = step
      }
    }
    else {
      folder = FolderFactory.getInstance().restoreForName(name)
    }

    if (!folder) error "No such folder <$name>"
    folder
  }
}

