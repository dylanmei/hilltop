package hilltop.commands

import hilltop.Config
import hilltop.finders.ProjectFinder
import com.urbancode.anthill3.domain.folder.*
import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.source.*

@Mixin(ConsoleHelper)
@Mixin(AnthillHelper)
class ProjectCommands {
  def config = new Config()
  ProjectFinder finder = new ProjectFinder()

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
      def projects = finder.all(inactive)
      projects.each { echo it.name }
    }
  }

  def list_in_folder(String name, inactive) {
    work {
      def folder = findFolder(name)
      folder.projects
        .findAll { f -> f.isActive != inactive }
        .each { p -> echo p.name }
    }
  }

  private Project findProject(projectName) {
    finder.project(projectName) {
      alert { m -> echo m }
      error { m -> quit m }
    }
  }

  private Folder findFolder(folderName) {
    finder.folder(folderName) {
      error { m -> quit m }
    }
  }
}