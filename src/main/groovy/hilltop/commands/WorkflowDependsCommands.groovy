
package hilltop.commands

import hilltop.anthill.*

class WorkflowDependsCommands extends AnthillCommands {
  def WorkflowDependsCommands(out) {
    super(out)
  }

  def list(projectName, workflowName) {
    send work {
      def workflow = finder(WorkflowFinder).one(projectName, workflowName)
      if (!workflow.isOriginating())
         return []

      def dependencies = workflow.buildProfile.dependencyArray

      dependencies.collect {[
        workflow_id: it.dependency.buildProfile.workflow.id,
        name: it.dependency.name,
        criteria: it.status.name,
        trigger: it.triggerType.description,
        artifacts: it.artifactSets.collect {[
          name: it.name
        ]}
      ]}
    }
  }

  def add(projectName, workflowName, dependencyWorkflowId, artifact, location) {
    work {
      def dependentWorkflow = finder(WorkflowFinder).one(projectName, workflowName)
      if (!dependentWorkflow.isOriginating()) 
        quit "Cannot add dependency to non-originating workflow <$dependentWorkflow.name>"
      
      
      def artifactSet = finder(ArtifactFinder).one(artifact);
      if (artifactSet == null) 
        quit "Cannot find artifact set <$artifact>"

      def dependencyWorkflow = finder(WorkflowFinder).one(dependencyWorkflowId)
      
      def dependencyFactory = new DependencyFactory({
         error { m -> quit m }
      })

      def dependency = dependencyFactory
         .create(dependentWorkflow, dependencyWorkflow, artifactSet, location)

      dependency.store()
    }
  }
}
