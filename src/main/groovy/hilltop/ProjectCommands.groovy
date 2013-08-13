package hilltop

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
        println "Description\t${project.getDescription()}"

      if (!project.isActive())
        println "Status\t\tInactive"

      def folder = project.getFolder()
      println "Folder\t\t${folder.getPath()}"

      def workflows = project.getWorkflowArray().sort { a, b ->
        if (a.isOriginating()) return b.isOriginating() ? 0 : -1
        if (b.isOriginating()) return a.isOriginating() ? 0 :  1
        a.getName() <=> b.getName()
      }
      println "Workflows\t${workflows.collect {w -> w.getName() + (w.isOriginating() ? '*' : '')}.join('\n\t\t')}"

      def sourceConfigType = project.getSourceConfigType()
      println "Source Config\t${sourceConfigType.getName().tokenize('.').last()}"

//      def configs = SourceConfigFactory.getInstance().restoreAllForProject(project)
//      configs.each {
//        println "${it.getId()}, ${it.getRepositoryUrl()}, ${it.getRepositoryName()}"
//      }

      def lifecycleModel = project.getLifeCycleModel()
      println "Lifecycle\t${lifecycleModel.getName()}"

      def environmentGroup = project.getEnvironmentGroup()
      println "Environment\t${environmentGroup.getName()}"
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

    if (url) open_browser(url)
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