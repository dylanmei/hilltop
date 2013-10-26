package hilltop.colonies

////http://groovy.codeplex.com/wikipage?title=Guillaume%20Laforge%27s%20%22Mars%20Rover%22%20tutorial%20on%20Groovy%20DSL%27s

class ColonyBuilder {
  def values = [:]

  def ColonyBuilder() {
    values['project'] = null
    values['workflows'] = []
  }

  def project(name, Closure settings = {}) {
    def p = new ProjectBuilder(name: name)
    settings.delegate = p; settings()
    values['project'] = p
  }

  def workflow(name, Closure settings = {}) {
    def w = new WorkflowBuilder(name: name)
    settings.delegate = w; settings()
    values['workflows'] << w
  }

  def build() {
    new Colony(
      project: values['project'].build(),
      workflows: values['workflows'].collect { it.build() }
    )
  }
}
