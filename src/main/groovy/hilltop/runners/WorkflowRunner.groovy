package hilltop.runners

import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.domain.buildrequest.*
import com.urbancode.anthill3.domain.buildlife.*
import com.urbancode.anthill3.domain.servergroup.*
import hilltop.Feedback

@Mixin(Feedback)
class WorkflowRunner {
  def buildlife
  def errorHandler

  public WorkflowRunner(buildlife, Closure handlers = null) {
    this.buildlife = buildlife
    if (handlers) init_feedback(handlers)
  }

  public BuildRequest request(name, environment, properties) {

    def project = buildlife.project
    def workflows = find_workflows_to_run(name, project.workflowArray)

    if (workflows.size() == 0)
      error "Cannot find runnable workflow <$name> for project <$project.name>"

    Workflow workflow
    ServerGroup server_group

    if (workflows.size() == 1)
      workflow = workflows.first()

    if (workflows.size() > 1)
      error "Multiple workflow matches not supported"

    def server_groups = find_environments_to_run_in(environment, workflows)

    if (server_groups.size() == 0)
      error "Cannot find environment <$environment> assigned to workflow <$workflow.name>"

    if (server_groups.size() == 1)
      server_group = server_groups.first()

    if (workflows.size() > 1)
      error "Multiple environment matches not supported"

    AnthillEngine.create_workflow_request(buildlife, workflow, server_group)
  }

  def find_workflows_to_run(name, workflows) {
    def runnable_workflows = workflows.findAll { !it.isOriginating() && it.isActive() }
    def best_match = runnable_workflows.find { it.name.matches(~"(?i)${name.trim()}") }
    best_match ? [best_match] :
      runnable_workflows.findAll { it.name.matches(~"(?i)${name.trim()}.*") }
  }

  def find_environments_to_run_in(name, workflows) {
    workflows
      .collect { it.serverGroupArray }
        .flatten()
      .findAll { it.name.matches(~"(?i)$name") }
      .unique { it.name.toLowerCase() }
  }

  public static void submit(BuildRequest request) {
    AnthillEngine.submit_workflow_request(request)
  }
}