package hilltop.commands

import hilltop.anthill.*
import com.urbancode.anthill3.domain.folder.*
import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.source.*

class ProjectCommands extends AnthillCommands {
  def ProjectCommands(out) {
    super(out)
  }

  def show(projectName) {
    send work {
      def project = finder(ProjectFinder).one(projectName)

      def workflows = project.getWorkflowArray().sort { a, b ->
        if (a.isOriginating()) return b.isOriginating() ? 0 : -1
        if (b.isOriginating()) return a.isOriginating() ? 0 :  1
        a.getName() <=> b.getName()
      }

      return [
        id: project.id,
        name: project.name,
        url: link_to(project),
        description: project.description,
        active: project.isActive(),
        folder: project.folder.path,
        workflows: workflows.collect {[
          id: it.id, name: it.name,
          url: link_to(it),
          originating: it.isOriginating(),
          mark: it.isOriginating()
        ]},
        environment: project.environmentGroup?.name,
        lifecycle: project.lifeCycleModel?.name,
        source_config: project.sourceConfigType?.name.tokenize('.').last(),
      ]
    }
  }

  def open(projectName, admin) {
    def project = work {
      finder(ProjectFinder).one(projectName)
    }

    browse link_to {
      resource project
      attributes admin: admin
    }
  }

  def list(inactive, folderName, nameFilter) {
    send work {
      def projects
      if (!folderName) {
        projects = finder(ProjectFinder).all(inactive)
      }
      else {
        def folder = finder(FolderFinder).one(folderName)
        projects = folder.projects
          .findAll { f -> f.isActive != inactive }
      }

      if (nameFilter) {
        def pattern = ~"(?i).*${nameFilter.trim()}.*"
        projects = projects.findAll {
          pattern.matcher(it.name).matches()
        }
      }

      return projects.collect {[
          id: it.id,
          name: it.name,
          url: link_to(it),
          description: it.description,
          active: it.isActive,
          folder: it.folder.path,
        ]
      }
    }
  }

  def remove(name) {
    work {
      def project = finder(ProjectFinder).one(name)
      try {
        project.delete()
        println "Project <$project.name> has been removed"
      }
      catch (RuntimeException re) {
       quit "Unable to remove project <$project.name>: $re.message"
      }
    }
  }
}
