package hilltop.colonies

class ProjectBuilder {
  def name
  def values = [:]

  def folder(name) {
    values['folder'] = name
  }

  def build() {
    new Project(
      name: name,
      folder: values['folder']
    )
  }
}
