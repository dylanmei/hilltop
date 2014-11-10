
package hilltop.commands

import hilltop.anthill.*
import com.urbancode.anthill3.domain.profile.*
import org.apache.commons.lang3.*

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
      if (!artifactSet) 
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
       
       if (!dependencyToRemove)
          quit "<$dependentProjectName>-<$dependentWorkflowName> does not depend on <$dependencyProjectName>-<$dependencyWorkflowName>"
       
       dependencyToRemove.delete()
    }
  }

  def setConflictStrategy(projectName, workflowName, conflictStrategyName) {
    work {
      def workflow = finder(WorkflowFinder).one(projectName, workflowName)

      if (!workflow.isOriginating()) 
        quit "Cannot set conflict strategy from non-originating workflow <$workflow.name>"
 
      def conflictStrategy = conflictEnumFromString(conflictStrategyName)
      if (!conflictStrategy)
        quit "No such conflict strategy <$conflictStrategyName>"

      workflow.buildProfile.dependencyConflictStrategy = conflictStrategy
      println "Set conflict strategy to <$conflictStrategy.name>"
    }
  }

  def setTrigger(projectName, workflowName, dependencyProjectName, dependencyWorkflowName, triggerName) {
    work {
      def workflow = finder(WorkflowFinder).one(projectName, workflowName)

      if (!workflow.isOriginating()) 
        quit "Cannot set dependency trigger from non-originating workflow <$workflow.name>"
 
      def trigger = toTrigger(triggerName)
      if (!trigger)
        quit "Unsupported dependency trigger <$triggerName>"

      def dependencyWorkflow = finder(WorkflowFinder).one(dependencyProjectName, dependencyWorkflowName)

      def dependency = workflow.buildProfile.dependencyArray
         .find { it.dependency.buildProfile == dependencyWorkflow.buildProfile }
       
       if (!dependency)
          quit "<$workflow.project.name ($workflow.name)> does not depend on <$dependencyProjectName ($dependencyWorkflowName)>"

      dependency.buildCondition = trigger
      println "Set dependency trigger to <$triggerName>"
    }
  }

  def conflictEnumFromString(text) {
    if (text != null) {
      text = noSpaces(text)
      for (BuildProfile.ConflictStratEnum e : BuildProfile.ConflictStratEnum.enumTypes) {
        def contains = StringUtils.containsIgnoreCase(noSpaces(e.name), text)
        if (contains)
          return e
      }
    }
    null
  }

  def toTrigger(text) {
    if (text != null) {
      text = noSpaces(text)
      if (StringUtils.containsIgnoreCase(text, 'push'))
        return 3
      if (StringUtils.containsIgnoreCase(text, 'existing'))
        return 1
    }
    null 
  }

  def noSpaces(text) {
    text.replaceAll("\\s", "")
  }
}