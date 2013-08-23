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
      def project = findProject(projectName)
      echo project.name

      if (project.description)
        echo "Description", project.description

      if (!project.isActive())
        echo "Status", "Inactive"

      def folder = project.getFolder()
      echo "Folder", folder.path

      def workflows = project.getWorkflowArray().sort { a, b ->
        if (a.isOriginating()) return b.isOriginating() ? 0 : -1
        if (b.isOriginating()) return a.isOriginating() ? 0 :  1
        a.getName() <=> b.getName()
      }

      echo "Workflows", { line ->
        workflows.each { w -> line.echo "${w.isOriginating() ? '*' : ' '} ${w.name}" }
      }

      def sourceConfigType = project.getSourceConfigType()
      echo "Source Config", sourceConfigType.name.tokenize('.').last()

      def lifecycleModel = project.getLifeCycleModel()
      echo "Lifecycle", lifecycleModel.name

      def environmentGroup = project.getEnvironmentGroup()
      echo "Environment", environmentGroup.name
    }
  }

  def open(projectName, admin) {
    def settings = config.get('anthill')
    def url = work {
      def project = findProject(projectName)
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
      projects.each { echo it.name }
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
          .each { p -> echo p.name }
      }
    }
  }

  private Project findProject(projectName) {
    def project = finder.project(projectName) {
      alert { m -> echo m }
      error { m -> quit m }
    }
  }
}