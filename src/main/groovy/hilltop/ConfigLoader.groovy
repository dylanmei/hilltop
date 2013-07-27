package hilltop

class ConfigLoader {
  def static final HOME = System.getProperty('user.home')

  def load() {
    def file = new File("$HOME/.hilltop")
    file.exists() ?
      new ConfigSlurper().parse(file.toURL()) : new ConfigObject()
  }

  def save(config) {
    def file = new File("$HOME/.hilltop")
    file.withWriter{ writer ->
      config.writeTo(writer)
    }
  }
}