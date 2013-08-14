package hilltop.cli

class Command {
  def name
  def description
  def path
  def config
  def execute
  def children = []
  def arguments = new CommandArguments()

  static Command Core() {
    new Command('')
  }

  static Command Sub(parent, name) {
    def child = parent.children.find { it.name == name }
    if (!child) {
      child = new Command(name, parent.path + ':' + name)
      parent.children << child
    }
    child
  }

  def Command(name, path = null) {
    this.name = name
    this.path = path ?: name
  }
}
