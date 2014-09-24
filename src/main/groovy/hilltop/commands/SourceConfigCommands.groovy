
package hilltop.commands

import hilltop.anthill.*
import com.urbancode.anthill3.domain.source.plugin.*

class WorkflowSourceCommands extends AnthillCommands {
  def WorkflowSourceCommands(out) {
    super(out)
  }

  def gitSetBranch(projectName, workflowName, branch) {
    send work {
      def sourceProps = getGitSourceProperties(projectName, workflowName)
      sourceProps.setPropertyValue('branch', branch, false)
      println "Set branch for <$workflowName> to <$branch>"
    }
  }

  def gitSetRemoteUrl(projectName, workflowName, remoteUrl) {
    send work {
      def sourceProps = getGitSourceProperties(projectName, workflowName)
      sourceProps.setPropertyValue('remoteUrl', remoteUrl, false)
      println "Set remote URL for <$workflowName> to <$remoteUrl>"
    }
  }

  def getGitSourceProperties(projectName, workflowName) {
      def workflow = finder(WorkflowFinder).one(projectName, workflowName)
      if (!workflow.isOriginating())
        quit "Cannot set branch on non-originating workflow <$workflowName>"

      def sourceConfig = workflow.buildProfile.sourceConfig
      def sourceConfigType = sourceConfig.sourceConfigType

      if (!sourceConfigType.name.endsWith('.plugin.PluginSourceConfig')) 
        quit "Unsupported source config type (<$sourceConfigType.name>)"

      def pluginConfig = sourceConfig.asType(PluginSourceConfig)
      def repository = pluginConfig.repositoryArray.first()
      def plugin = repository.plugin

      if (!plugin.pluginId.endsWith('.plugin.Git'))
         quit "Unsupported plugin ID (<$plugin.pluginId>)"

      def sourceProps = pluginConfig.sourcePropertyValueGroups.first()
  }
}
