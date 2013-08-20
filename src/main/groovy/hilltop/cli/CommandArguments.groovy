package hilltop.cli

class CommandArguments {
  def exactly = 0
  def minimum = 0
  def name = 'arg'
  def extraNames = [:]

  def getNames() {
    def arguments = []
    if (exactly == 1) {
      arguments << name
    }
    else if (exactly > 1) {
      arguments += (1..exactly).collect { getProperty('name' + it) }
    }
    else if (minimum > 0) {
      arguments << name
    }
    arguments
  }

  def propertyMissing(String name, value) {
    def matcher = (name =~ /name([1-9]\d?)/)
    if (matcher.matches())
      extraNames[name] = value
  }
  
  def propertyMissing(String name) {
    def matcher = (name =~ /name([1-9]\d?)/)
    if (matcher.matches()) {
      def argName = extraNames[name]
      return argName ?: this.name + matcher[0][1]
    }
    null
  } 
}