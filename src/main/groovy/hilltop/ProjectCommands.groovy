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
      def projects = ProjectFactory.getInstance()
        .restoreAllForName(name)
      if (projects.size() > 1) {
        println "There are #{projects.size()} named <${name}>; taking the first one)"
      }

      if (projects.size() == 0) {
        println "No such project <${name}>"; return
      }

      def project = projects[0]
      def folder = project.getFolder()
      def workflows = project.getOriginatingWorkflowArray()
      def hint = project.isActive() ? '' : ' [inactive]'
      println "${project.getName()}${hint}"
      println "Folder\t\t${folder.getPath()}"
      println "Workflows\t${workflows.collect {w -> w.getName()}.join('\n\t\t')}"
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

  private work(Closure task) {
    def token = config.get('token')
    def server = config.get('server')

    def client = AnthillClient.connect(server, 4567, token)
    def uow = client.createUnitOfWork()
    def result = task()
    uow.commitAndClose();
    result
  }
}