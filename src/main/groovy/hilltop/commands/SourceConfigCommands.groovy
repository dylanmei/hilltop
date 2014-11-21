
package hilltop.commands

import hilltop.anthill.*
import com.urbancode.anthill3.domain.source.plugin.*

class WorkflowSourceCommands extends AnthillCommands {
  def WorkflowSourceCommands(out) {
    super(out)
  }

  def setSourceConfig(projectName, workflowName, sourceType, properties) {
    if (!sourceType.equalsIgnoreCase('git'))
      quit "Unsupported source type <$sourceType>"
    
    def propertyMap = PropertyHelper.toMap(properties)

    work {
      propertyMap.each { key, value ->
        if (key.equalsIgnoreCase('branch'))
          gitSetBranch(projectName, workflowName, value)
        else if (key.equalsIgnoreCase('remote-url'))
          gitSetRemoteUrl(projectName, workflowName, value)
        else
          println "Ignoring property <$key> (supported values are ['branch', 'remote-url'])"
      }
    }
  }

  def gitSetBranch(projectName, workflowName, branch) {
    def sourceProps = getGitSourceProperties(projectName, workflowName)
    sourceProps.setPropertyValue('branch', branch, false)
    println "Set branch for <$workflowName> to <$branch>"
  }

  def gitSetRemoteUrl(projectName, workflowName, remoteUrl) {
    def sourceProps = getGitSourceProperties(projectName, workflowName)
    sourceProps.setPropertyValue('remoteUrl', remoteUrl, false)
    println "Set remote URL for <$workflowName> to <$remoteUrl>"
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
