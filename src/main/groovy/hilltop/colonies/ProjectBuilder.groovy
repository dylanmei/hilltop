package hilltop.colonies

import hilltop.anthill.*
import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.folder.*

class BuilderException extends GroovyRuntimeException {
  def BuilderException(String message) {
    super(message)
  }
}

class ProjectBuilder {
  def name
  def values = [:]

  def folders = new FolderFinder({
    error { m -> throw new BuilderException(m) }
  })
  def lifecycles = new LifecycleFinder({
    error { m -> throw new BuilderException(m) }
  })
  def environments = new EnvironmentFinder({
    error { m -> throw new BuilderException(m) }
  })

  def folder(value) {
    values['folder'] = value
  }
  def description(value) {
    values['description'] = value
  }
  def lifecycle(value) {
    values['lifecycle'] = value
  }
  def environment(value) {
    values['environment'] = value
  }

  def build() {
    def project = new Project(name)
    project.description = values['description']
    project.folder = folders.one(values['folder'])
    project.lifeCycleModel = lifecycles.one(values['lifecycle'])
    project.environmentGroup = environments.group(values['environment'])
    project
  }
}
