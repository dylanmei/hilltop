
package hilltop.commands

import hilltop.anthill.*
import com.urbancode.anthill3.domain.workflow.*

class WorkflowPropertyCommands extends AnthillCommands {
  def WorkflowPropertyCommands(out) {
    super(out)
  }

  def list(projectName, workflowName) {
    send work {
      def workflow = finder(WorkflowFinder).one(projectName, workflowName)

      workflow.propertyArray.collect {[
        name: it.name,
        value: it.propertyValue.value
      ]}
    }
  }

  def add(projectName, workflowName, propertyName, propertyValue) {
    work {
      def workflow = finder(WorkflowFinder).one(projectName, workflowName)

      def spec = new WorkflowProperty(propertyName)
      spec.setPropertyValue(propertyValue, false) // no encryption
      workflow.addProperty(spec)
    }
  }

  def remove(projectName, workflowName, propertyName) {
    work {
      def workflow = finder(WorkflowFinder).one(projectName, workflowName)

      def spec = new WorkflowProperty(propertyName)
      workflow.removeProperty(spec)
    }
  }
}
