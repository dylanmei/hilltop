
package hilltop.commands

import hilltop.anthill.*
import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.project.prop.*

class ProjectPropertyCommands extends AnthillCommands {
  def ProjectPropertyCommands(out) {
    super(out)
  }

  def list(projectName) {
    send work {
      def project = finder(ProjectFinder).one(projectName)
      project.propertyArray.collect {[
        name: it.name,
        value: it.value
      ]}
    }
  }

  def add(projectName, propertyName, propertyValue) {
    work {
      def project = finder(ProjectFinder).one(projectName)
      addProperty(project, propertyName, propertyValue)
    }
  }

  def remove(projectName, propertyName) {
    work {
      def project = finder(ProjectFinder).one(projectName)
      project.removeProperty(propertyName)
    }
  }

  def set(projectName, propertyName, propertyValue) {
    work {
      def project = finder(ProjectFinder).one(projectName)
      def property = project.propertyArray.find { p -> p.name == propertyName}

      if (property)
        property.setValue(propertyValue)
      else
        addProperty(project, propertyName, propertyValue)
    }
  }

  def addProperty(project, propertyName, propertyValue) {
     def spec = new ProjectProperty(propertyName)
     spec.setValue(propertyValue)
     project.addProperty(spec)
  }
}