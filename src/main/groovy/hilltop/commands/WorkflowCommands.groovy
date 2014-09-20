
package hilltop.commands

import hilltop.anthill.*
import com.urbancode.codestation2.domain.project.*
import com.urbancode.anthill3.domain.profile.*
import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.domain.source.*
import com.urbancode.anthill3.domain.source.plugin.*
import com.urbancode.anthill3.domain.buildlife.*

class WorkflowCommands extends AnthillCommands {
  def WorkflowCommands(out) {
    super(out)
  }

  def show(projectName, workflowName) {
    send work {
      def workflow = finder(WorkflowFinder).one(projectName, workflowName)
      def project = workflow.project
      def definition = workflow.workflowDefinition

      def result = [
        id: workflow.id,
        name: workflow.name,
        url: link_to(workflow),
        project: project.name,
        description: workflow.description,
        originating: workflow.isOriginating(),
        lifecycle: definition.lifeCycleModel?.name,
      ]

      if (!workflow.isOriginating()) {
        result['environments'] = workflow.serverGroupArray.collect { it.name }
      }
      else {
        def buildProfile = workflow.buildProfile
        def sourceConfig = buildProfile.sourceConfig
        def sourceConfigType = sourceConfig.sourceConfigType

        if (sourceConfigType.name.endsWith('.plugin.PluginSourceConfig')) {
          def pluginConfig = sourceConfig.asType(PluginSourceConfig)
          def repository = pluginConfig.repositoryArray.first()
          def plugin = repository.plugin
          result['source_type'] = repository.typeName

          if (plugin.pluginId.endsWith('.plugin.Git')) {
            def repoProps = repository.getPropertyValueGroupsWithType('repo').first().propertyMap
            def sourceProps = pluginConfig.sourcePropertyValueGroups.first().propertyMap

            result['repository_url'] = repoProps['repoBaseUrl'].toString() + sourceProps['remoteUrl'].toString()
            result['repository_branch'] = sourceProps['branch'].toString()
          }
        }
        else {
          if (sourceConfigType.name.endsWith('.git.GitSourceConfig')) {
            result['source_type'] = sourceConfig.repository.name
            result['repository_url'] = sourceConfig.repositoryUrl
            result['repository_branch'] = sourceConfig.revision
          }
          else {
            result['source_type'] = sourceConfigType.name - 'com.urbancode.anthill3.domain.source.'
            if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getRepositoryName'))
              result['repository_name'] = sourceConfig.repositoryName
            if (sourceConfig.metaClass.respondsTo(sourceConfig, 'getWorkspaceName'))
              result['workspace_name'] = sourceConfig.workspaceName
          }
        }
      }

      return result
    }
  }

  def open(projectName, workflowName, admin) {
    def workflow = work {
      finder(WorkflowFinder).one(projectName, workflowName)
    }

    browse link_to {
      resource workflow
      attributes admin: admin
    }
  }

  def list(projectName, inactive) {
    send work {
      def project = finder(ProjectFinder).one(projectName)
      def workflows = finder(WorkflowFinder).all(project, inactive).sort { a, b ->
        if (a.isOriginating()) return b.isOriginating() ? 0 : -1
        if (b.isOriginating()) return a.isOriginating() ? 0 :  1
        a.getName() <=> b.getName()
      }

      workflows.collect {[
        id: it.id,
        name: it.name,
        url: link_to(it),
        description: it.description,
        originating: it.isOriginating(),
      ]}
    }
  }

  def list_dependencies(projectName, workflowName) {
    send work {
      def workflow = finder(WorkflowFinder).one(projectName, workflowName)
      if (!workflow.isOriginating())
         return []

      def dependencies = workflow.buildProfile.dependencyArray

      def result = dependencies.collect {[
        workflow_id: it.dependency.buildProfile.workflow.id,
        name: it.dependency.name,
        criteria: it.status.name,
        trigger: it.triggerType.description,
        artifacts: it.artifactSets.collect {[
          name: it.name
        ]}
      ]}

      return result
    }
  }

  def add_dependency(projectName, workflowName, dependencyWorkflowId, artifact, location) {
    send work {
      def dependentWorkflow = finder(WorkflowFinder).one(projectName, workflowName)
      if (!dependentWorkflow.isOriginating()) {
        println "Cannot add dependency to non-originating workflow <$dependentWorkflow.name>!"
        return
      }
      
      def artifactSet = finder(ArtifactFinder).one(artifact);
      if (artifactSet == null) {
        println "Cannot find artifact set <$artifact>"
        return
      }
      def dependentProject = new AnthillProject(dependentWorkflow.buildProfile)

      def dependencyWorkflow = finder(WorkflowFinder).one(dependencyWorkflowId)
      def dependencyProject = new AnthillProject(dependencyWorkflow.buildProfile)

      def dependency = new Dependency(dependentProject, dependencyProject)
      dependency.setBuildConditionToDefault()
      dependency.status = finder(StatusFinder).one(
        "success", dependentWorkflow.workflowDefinition.lifeCycleModel)
      def tranisitive = false;
      dependency.addSet2Dir(artifactSet, location, tranisitive);

      dependency.store()
    }
  }

  def remove(projectName, workflowName, force, noop) {
    def workflow, project
    work {
      workflow = finder(WorkflowFinder).one(projectName, workflowName)
      project = workflow.project
    }

    def destroyer = new WorkflowDestroyer(connect(), {
      error { m -> quit m }
      alert { m -> println m }
    })

    destroyer.go(workflow, force, noop)
    println "Workflow <$workflow.name> has been removed from Project <$workflow.project.name>"
  }
}
