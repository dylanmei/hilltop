package hilltop

import com.urbancode.anthill3.main.client.AnthillClient;
import com.urbancode.anthill3.persistence.UnitOfWork;
import com.urbancode.anthill3.domain.folder.*;
import com.urbancode.anthill3.domain.project.*;

class ProjectCommands {
  def config

  def ProjectCommands(config) {
    this.config = config
  }

  def show(name) {
    work {
      def project = get_project_or_complain(name)
      if (!project) return

      def folder = project.getFolder()
      def workflows = project.getOriginatingWorkflowArray()
      def hint = project.isActive() ? '' : ' [inactive]'
      println "${project.getName()}${hint}"
      println "Folder\t\t${folder.getPath()}"
      println "Workflows\t${workflows.collect {w -> w.getName()}.join('\n\t\t')}"
    }
  }

  def open(name, admin) {
    def settings = config.get('anthill')
    def url = work {
      def project = get_project_or_complain(name)
      if (!project) return

      return admin ?
        "http://${settings.api_server}:8181/tasks/admin/project/ProjectTasks/viewProject?projectId=${project.id}" :
        "http://${settings.api_server}:8181/tasks/project/ProjectTasks/viewDashboard?projectId=${project.id}"
    }

    if (url) {
      Class<?> d = Class.forName("java.awt.Desktop");
      d.getDeclaredMethod("browse", [java.net.URI.class] as Class[]).invoke(
      d.getDeclaredMethod("getDesktop").invoke(null), [java.net.URI.create(url)] as Object[]);
    }
  }

  def list(inactive) {
    work {
      def projects = inactive ?
        ProjectFactory.getInstance().restoreAll() :
        ProjectFactory.getInstance().restoreAllActive()
      projects.each { p ->
        def hint = p.isActive() ? '' : ' [inactive]'
        println "${p.getName()}${hint}"
      }
    }
  }

  def list_folder(name, inactive) {
    work {
      def folder = FolderFactory.getInstance().restoreForName(name)
      if (!folder)
        println "No such folder <${name}>"
      else {
        folder.getProjects()
          .findAll { f -> f.isActive }
          .each { p -> println "${p.getName()}" }
      }
    }
  }

  private get_project_or_complain(name) {
      def projects = ProjectFactory.getInstance()
        .restoreAllForName(name)
      if (projects.size() > 1) {
        println "There are #{projects.size()} named <${name}>; taking the first one)"
      }

      if (projects.size() == 0) {
        println "No such project <${name}>"; return
      }

      projects[0]
  }

  private work(Closure task) {
    def settings = config.get('anthill')
    def client = AnthillClient.connect(settings.api_server, 4567, settings.api_token)
    def uow = client.createUnitOfWork()
    def result = task()
    uow.commitAndClose();
    result
  }
}