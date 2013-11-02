package hilltop.commands

import com.urbancode.anthill3.main.client.AnthillClient;
import com.urbancode.anthill3.persistence.UnitOfWork;
import com.urbancode.anthill3.domain.persistent.Persistent;

class AnthillHelper {
  protected link_to(Closure link) {
    def settings = config.get('anthill')
    def result = new AnthillLink(settings)
    link.delegate = result; link()
    result.url()
  }

  protected link_to(Persistent anthill_object) {
    link_to {
      resource anthill_object
    }
  }

  protected work(Closure task) {
    def settings = config.get('anthill')
    if (settings == null || settings.api_token.isEmpty() || settings.api_server.isEmpty()) {
      quit 'Your Anthill configuration requires anthill.api_server and anthill.api_token values.'
    }

    def result
    def client = AnthillClient.connect(settings.api_server, 4567, settings.api_token)
    def uow = client.createUnitOfWork()
    try {
      result = task(uow)
    }
    catch (Exception e) {
      if (!uow.isClosed())
        uow.cancel()
      throw e
    }

    uow.commitAndClose();
    result
  }

  class AnthillLink {
    def obj
    def map = [:]
    def config

    def AnthillLink(config) {
      this.config = config
    }

    def resource(obj) {
      this.obj = obj
    }

    def attributes(map) {
      this.map = map
    }

    def url() {
      def className = obj.class.name
      if (className ==~ /.*\.Project$/)
        return project_link()
      if (className ==~ /.*\.Workflow$/)
        return workflow_link()
      if (className ==~ /.*\.BuildRequest$/)
        return request_link()
      if (className ==~ /.*\.BuildLife$/)
        return buildlife_link()
      if (className ==~ /.*\.ServerGroup$/)
        return environment_link()
      if (className ==~ /.*\.LifeCycleModel$/)
        return lifecycle_link()

      throw new GroovyRuntimeException("Cannot create a link for an unknown object: " + className)
    }

    def project_link() {
      def admin = map['admin'] ?: false
      return admin ?
        "http://$config.api_server:8181/tasks/admin/project/ProjectTasks/viewProject?projectId=$obj.id" :
        "http://$config.api_server:8181/tasks/project/ProjectTasks/viewDashboard?projectId=$obj.id"
    }

    def workflow_link() {
      def admin = map['admin'] ?: false
      return admin ?
          "http://$config.api_server:8181/tasks/admin/project/workflow/WorkflowTasks/viewWorkflow?workflowId=$obj.id" :
          "http://$config.api_server:8181/tasks/project/WorkflowTasks/viewDashboard?workflowId=$obj.id"
    }

    def request_link() {
      "http://$config.api_server:8181/tasks/project/BuildRequestTasks/viewBuildRequest?buildRequestId=$obj.id"
    }

    def buildlife_link() {
      "http://$config.api_server:8181/tasks/project/BuildLifeTasks/viewBuildLife?buildLifeId=$obj.id"
    }

    def environment_link() {
      "http://$config.api_server:8181/tasks/admin/servergroup/ServerGroupTasks/viewServerGroup?serverGroupId=$obj.id"
    }

    def lifecycle_link() {
      "http://$config.api_server:8181/tasks/admin/lifecyclemodel/LifeCycleModelTasks/viewLifeCycleModel?lifeCycleModelId=$obj.id"
    }
  }
}
