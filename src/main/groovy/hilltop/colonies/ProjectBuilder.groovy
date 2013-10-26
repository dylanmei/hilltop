package hilltop.colonies

import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.folder.*

class ProjectBuilder {
  def name
  def values = [:]

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

    if (!values['folder'])
      values['folder'] = '/'

    def folder = new Folder(true)
    folder.name = values['folder']
    project.folder = folder

    project
  }
}
