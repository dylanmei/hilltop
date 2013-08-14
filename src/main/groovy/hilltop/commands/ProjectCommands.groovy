package hilltop.commands

import com.urbancode.anthill3.domain.folder.*;
import com.urbancode.anthill3.domain.project.*;
import com.urbancode.anthill3.domain.source.*;

class ProjectCommands extends AnthillCommands {
  def config

  def ProjectCommands(config) {
    this.config = config
  }

  def show(name) {
    work {
      def project = get_project_or_complain(name)
      println project.getName()


      if (project.getDescription())
        println "Description".padRight(40) + project.description

      if (!project.isActive())
        println "Status".padRight(40) + "Inactive"

      def folder = project.getFolder()
      println "Folder".padRight(40) + folder.path

      def workflows = project.getWorkflowArray().sort { a, b ->
        if (a.isOriginating()) return b.isOriginating() ? 0 : -1
        if (b.isOriginating()) return a.isOriginating() ? 0 :  1
        a.getName() <=> b.getName()
      }
      println "Workflows".padRight(40) + "${workflows.collect {w -> w.getName() + (w.isOriginating() ? '*' : '')}.join('\n' + (' ') * 40)}"

      def sourceConfigType = project.getSourceConfigType()
      println "Source Config".padRight(40) + sourceConfigType.name.tokenize('.').last()

      def lifecycleModel = project.getLifeCycleModel()
      println "Lifecycle".padRight(40) + lifecycleModel.name

      def environmentGroup = project.getEnvironmentGroup()
      println "Environment".padRight(40) + environmentGroup.name
    }
  }

  def open(name, admin) {
    def settings = config.get('anthill')
    def url = work {
      def project = get_project_or_complain(name)
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
      projects.each { println it.getName() }
    }
  }

  def list_folder(name, inactive) {
    work {
      def folder = FolderFactory.getInstance().restoreForName(name)
      if (!folder)
        println "No such folder <${name}>"
      else {
        folder.getProjects()
          .findAll { f -> f.isActive != inactive }
          .each { p -> println "${p.getName()}" }
      }
    }
  }
}