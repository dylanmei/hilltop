package hilltop.colonies

class WorkflowBuilder {
  def name
  def values = [:]

  def originating(value) {
    values['originating'] = value 
  }

  def build() {
    new Workflow(
      name: name,
      originating: values['originating'] ?: false
    )
  }
}

