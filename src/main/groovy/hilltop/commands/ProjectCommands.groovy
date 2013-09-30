package hilltop.commands

import hilltop.Config
import hilltop.anthill.ProjectFinder
import com.urbancode.anthill3.domain.folder.*
import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.source.*

@Mixin(ConsoleHelper)
@Mixin(AnthillHelper)
class ProjectCommands {
  def config = new Config()
  def finder = new ProjectFinder({
    alert { m -> echo m }; error { m -> quit m }
  })

  def show(projectName) {
    work {
      def project = finder.project(projectName)
      echo project.name
      echo link(project, false)

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
      def project = finder.project(projectName)
      link(project, admin)
    }

    browse url
  }

  def list(inactive, folderName) {
    work {
      def projects
      if (!folderName) projects = finder.all(inactive)
      else {
        def folder = finder.folder(folderName)
        projects = folder.projects
          .findAll { f -> f.isActive != inactive }
      }

      projects.each { echo it.name }
    }
  }

  def link(project, admin) {
    def settings = config.get('anthill')
    return admin ?
      "http://${settings.api_server}:8181/tasks/admin/project/ProjectTasks/viewProject?projectId=${project.id}" :
      "http://${settings.api_server}:8181/tasks/project/ProjectTasks/viewDashboard?projectId=${project.id}"
  }
}