package hilltop.commands

import hilltop.finders.ProjectFinder
import com.urbancode.anthill3.domain.folder.*
import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.source.*

@Mixin(ConsoleCommands)
@Mixin(AnthillCommands)
class ProjectCommands {
  def config

  ProjectFinder finder = new ProjectFinder()

  def ProjectCommands(config) {
    this.config = config
  }

  def show(projectName) {
    work {
      def project = getProject(projectName)
      echo project.name

      if (project.description)
        echo "Description".padRight(40) + project.description

      if (!project.isActive())
        echo "Status".padRight(40) + "Inactive"

      def folder = project.getFolder()
      echo "Folder".padRight(40) + folder.path

      def workflows = project.getWorkflowArray().sort { a, b ->
        if (a.isOriginating()) return b.isOriginating() ? 0 : -1
        if (b.isOriginating()) return a.isOriginating() ? 0 :  1
        a.getName() <=> b.getName()
      }
      echo "Workflows".padRight(40) + "${workflows.collect {w -> w.getName() + (w.isOriginating() ? '*' : '')}.join('\n' + (' ') * 40)}"

      def sourceConfigType = project.getSourceConfigType()
      echo "Source Config".padRight(40) + sourceConfigType.name.tokenize('.').last()

      def lifecycleModel = project.getLifeCycleModel()
      echo "Lifecycle".padRight(40) + lifecycleModel.name

      def environmentGroup = project.getEnvironmentGroup()
      echo "Environment".padRight(40) + environmentGroup.name
    }
  }

  def open(projectName, admin) {
    def settings = config.get('anthill')
    def url = work {
      def project = getProject(projectName)
      return admin ?
        "http://${settings.api_server}:8181/tasks/admin/project/ProjectTasks/viewProject?projectId=${project.id}" :
        "http://${settings.api_server}:8181/tasks/project/ProjectTasks/viewDashboard?projectId=${project.id}"
    }

    browse url
  }

  def list(inactive) {
    work {
      def projects = inactive ?
        ProjectFactory.getInstance().restoreAll() :
        ProjectFactory.getInstance().restoreAllActive()
      if (inactive)
        projects = projects.findAll { p -> !p.isActive }
      projects.each { echo it.getName() }
    }
  }

  def list_folder(name, inactive) {
    work {
      def folder = FolderFactory.getInstance().restoreForName(name)
      if (!folder)
        echo "No such folder <${name}>"
      else {
        folder.getProjects()
          .findAll { f -> f.isActive != inactive }
          .each { p -> echo "${p.getName()}" }
      }
    }
  }

  private Project getProject(projectName) {
    def project = finder.project(projectName) {
      alert { m -> echo m }
      error { m -> quit m }
    }    
  }
}