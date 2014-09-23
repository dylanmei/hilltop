
package hilltop.commands

import hilltop.anthill.*

class WorkflowDependencyCommands extends AnthillCommands {
  def WorkflowDependencyCommands(out) {
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
        project: it.dependency.buildProfile.project.name,
        workflow: it.dependency.buildProfile.workflow.name,
        criteria: it.status.name,
        trigger: it.triggerType.description,
        artifacts: it.artifactSets.collect {[
          name: it.name
        ]}
      ]}
    }
  }

  def add(
    dependentProjectName, dependentWorkflowName, 
    dependencyProjectName, dependencyWorkflowName, 
    artifact, location) {
    work {
      def dependentWorkflow = finder(WorkflowFinder).one(dependentProjectName, dependentWorkflowName)
      def dependencyWorkflow = finder(WorkflowFinder).one(dependencyProjectName, dependencyWorkflowName)

      if (!dependentWorkflow.isOriginating()) 
        quit "Cannot add dependency to non-originating workflow <$dependentWorkflow.name>"
     
      def artifactSet = finder(ArtifactFinder).one(artifact);
      if (artifactSet == null) 
        quit "Cannot find artifact set <$artifact>"
      
      def dependencyFactory = new DependencyCreator({
         error { m -> quit m }
      })

      def dependency = dependencyFactory
         .create(dependentWorkflow, dependencyWorkflow, artifactSet, location + "/$dependencyProjectName")

      dependency.store()
    }
  }

  def remove(
    dependentProjectName, dependentWorkflowName, 
    dependencyProjectName, dependencyWorkflowName) {
    work {
      def dependentWorkflow = finder(WorkflowFinder).one(dependentProjectName, dependentWorkflowName)
      def dependencyWorkflow = finder(WorkflowFinder).one(dependencyProjectName, dependencyWorkflowName)

      if (!dependentWorkflow.isOriginating()) 
        quit "Cannot remove dependency from non-originating workflow <$dependentWorkflow.name>"

      def dependencyToRemove = dependentWorkflow.buildProfile.dependencyArray
         .find { it.dependency.buildProfile == dependencyWorkflow.buildProfile }
       
       if (dependencyToRemove == null)
          quit "Could not find dependency <$dependencyName> on workflow <$dependentWorkflow.name>"
       
       dependencyToRemove.delete()
    }
  }
}
