package hilltop.commands

import hilltop.anthill.*
import com.urbancode.anthill3.domain.folder.*
import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.source.*

class ProjectCommands extends AnthillCommands {
  def projectFinder = Finder(ProjectFinder)
  def folderFinder = Finder(FolderFinder)

  def show(projectName) {
    work {
      def project = projectFinder.one(projectName)
      echo project, uri: link_to(project)

      if (project.description)
        echo "Description", project.description

      echo "Status", project.isActive() ? "Active" : "Inactive"
      echo "Folder", project.folder.path

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
    def project = work {
      projectFinder.one(projectName)
    }

    browse link_to {
      resource project
      attributes admin: admin
    }
  }

  def list(inactive, folderName) {
    work {
      def projects
      if (!folderName) projects = projectFinder.all(inactive)
      else {
        def folder = folderFinder.one(folderName)
        projects = folder.projects
          .findAll { f -> f.isActive != inactive }
      }

      projects.each { echo it.name }
    }
  }

  def remove(name) {
    work {
      def project = projectFinder.one(name)
      try {
        project.delete()
        echo "Project <$project.name> has been removed"
      }
      catch (RuntimeException re) {
       quit "Unable to remove project <$project.name>: $re.message"
      }
    }    
  }
}