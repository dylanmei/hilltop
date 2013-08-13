package hilltop

import com.urbancode.anthill3.main.client.AnthillClient;
import com.urbancode.anthill3.persistence.UnitOfWork;
import com.urbancode.anthill3.domain.folder.*;
import com.urbancode.anthill3.domain.project.*;
import com.urbancode.anthill3.domain.source.*;

class ProjectCommands extends Commands {
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

  private get_project_or_complain(name) {
    if (name == '.') name = infer_project_name(name)

    def projects = ProjectFactory.getInstance()
      .restoreAllForName(name)

    if (projects.size() > 1)
      println "There are ${projects.size()} named <$name>; taking the first one"
    if (projects.size() == 0)
      quit "No such project <$name>"

    projects[0]
  }

  private String infer_project_name(name) {
    if (name != '.') return name
    System.getProperty("user.dir")
      .tokenize(File.separator).last()
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