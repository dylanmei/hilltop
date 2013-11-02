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
      def project = finder.one(projectName)
      echo project.name
      echo link_to(project)

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

      if (project.sourceConfigType)
        echo "Source Config", project.sourceConfigType.name.tokenize('.').last()

      if (project.lifeCycleModel)
        echo "Lifecycle", project.lifeCycleModel.name

      def environmentGroup = project.getEnvironmentGroup()
      echo "Environment", environmentGroup.name
    }
  }

  def open(projectName, admin) {
    def settings = config.get('anthill')
    def project = work {
      finder.one(projectName)
    }

    browse link_to {
      resource project
      attributes admin: admin
    }
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
}