package hilltop.anthill

import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.domain.buildrequest.*
import com.urbancode.anthill3.domain.buildlife.*
import com.urbancode.anthill3.domain.servergroup.*

@Mixin(FeedbackHelper)
class WorkflowRunner {
  def buildlife

  public WorkflowRunner(buildlife, Closure handlers = null) {
    this.buildlife = buildlife
    if (handlers) init_feedback(handlers)
  }

  public BuildRequest request(name, environment, properties) {

    def project = buildlife.project
    def workflows = find_workflows_to_run(name, project.workflowArray)

    if (workflows.size() == 0)
      error "Cannot find runnable workflow <$name> for project <$project.name>"

    ServerGroup server_group

    def server_groups = find_environments_to_run_in(environment, workflows)

    if (server_groups.size() == 0)
      error "Cannot find environment <$environment> assigned to workflow <$name>"

    if (server_groups.size() == 1)
      server_group = server_groups.first()

    if (server_groups.size() > 1)
      error "Multiple environment matches not supported"

    workflows = workflows.findAll { w ->
      w.serverGroupArray.find { it.id == server_group.id } != null
    }

    if (workflows.size() > 1)
      error "Multiple workflow matches not supported"

    def workflow = workflows.first()
    AnthillEngine.create_workflow_request(buildlife, workflow, server_group, properties)
  }

  public BuildRequest requestForOperationalWorkflow(workflow, environment, properties) {
    
    ServerGroup server_group

    def server_groups = find_environments_to_run_in(environment, [workflow])

    if (server_groups.size() == 0)
      error "Cannot find environment <$environment> assigned to workflow <$workflow.name>"

    if (server_groups.size() == 1)
      server_group = server_groups.first()

    if (server_groups.size() > 1)
      error "Multiple environment matches not supported"

    AnthillEngine.create_operational_request(workflow, server_group, properties)
  }

  def find_workflows_to_run(name, workflows) {
    def runnable_workflows = workflows.findAll { !it.isOriginating() && it.isActive() }
    def best_match = runnable_workflows.find { it.name.matches(~"(?i)${name.trim()}") }
    best_match ? [best_match] :
      runnable_workflows.findAll { it.name.matches(~"(?i)${name.trim()}.*") }
  }

  def find_environments_to_run_in(name, workflows) {
    def environments = workflows
      .collect { it.serverGroupArray }
      .flatten()
      .unique { it.name.toLowerCase() }
    def best_match = environments
      .find { it.name.matches(~"(?i)$name") }
    best_match ? [best_match] :
      environments.findAll { it.name.matches(~"(?i)${name.trim()}.*") }
  }

  public static void submit(BuildRequest request) {
    AnthillEngine.submit_workflow_request(request)
  }
}