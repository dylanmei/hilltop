package hilltop.anthill

import com.urbancode.anthill3.domain.profile.*
import com.urbancode.anthill3.domain.persistent.*
import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.domain.buildrequest.*
import com.urbancode.anthill3.domain.buildlife.*
import com.urbancode.anthill3.domain.servergroup.*

@Mixin(FeedbackHelper)
class WorkflowDestroyer {
  def client
  def finder = new BuildFinder()

  public WorkflowDestroyer(client, Closure handlers = null) {
    this.client = client
    if (handlers) init_feedback(handlers)
  }

  public void go(Workflow workflow, force, noop) {
    work {
      check_no_dependant_projects(workflow)
      check_no_pending_requests(workflow)
    }

    if (force) {
      delete_non_preflight_builds(workflow, noop)
      delete_builds_from_profile(workflow, noop)
    }

    delete_workflow(workflow, noop)
  }

  private void delete_non_preflight_builds(workflow, noop) {
    def buildlifes = work {
      BuildLifeFactory.getInstance()
        .getNonPreflightIdsForWorkflow(workflow)
        .toList()
        .reverse()
    }

    if (buildlifes.size())
      alert "Removing non-preflight Buildlife(s)..."

    def index = 0, count = buildlifes.size()
    while (buildlifes.size()) {
      work {
        def build = finder.one(buildlifes.pop())
        alert "[${++index} of $count] Buildlife <$build.id>"
        if (!noop) build.delete()
      }
    }
  }

  private void delete_builds_from_profile(workflow, noop) {
    def profile = workflow.buildProfile
    if (!profile) return

    work {
      def buildlifes = BuildLifeFactory.getInstance()
        .restoreAllForProfile(profile)

      if (buildlifes.size() > 0) {
        alert "Removing Buildlife(s) from profile..."
        def index = 0, count = buildlifes.size()

        buildlifes.each { build ->
          alert "[${++index} of $count] Buildlife <$build.id>"
          if (!noop) build.delete()
        }
      }
    }
  }

  private void delete_workflow(workflow, noop) {
    "Removing Workflow..."
    work {
      try {
        if (!noop) workflow.delete()
      }
      catch (UnableToDeleteException de) {
        error "Unable to remove Workflow <$workflow.name> from Project <$workflow.project.name>: $de.message"
      }
    }
  }

  private void check_no_dependant_projects(workflow) {
    if (!workflow.buildProfile) return
    def dependants = DependencyFactory.getInstance()
      .getDependentNamesForProfile(workflow.buildProfile)
    if (dependants.size() > 0)
      error "Cannot remove a Workflow that is a dependency of other Projects"
  }

  private void check_no_pending_requests(workflow) {
    if (!workflow.buildProfile) return
    def requests = BuildRequestFactory.getInstance()
      .restoreBuildLifeRequestsForBuildProfile(workflow.buildProfile)
      .findAll { it.status == null || it.status.isComplete() == false }
    if (requests.size() > 0)
      error "Cannot remove a Workflow that has pending Build requests"
  }

  private Object work(Closure task) {
    AnthillEngine.submit_work(client, task)
  }
}