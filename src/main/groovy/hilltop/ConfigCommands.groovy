package hilltop

class ConfigCommands {
  def config

  def ConfigCommands(config) {
  	this.config = config
  }

  def get(name) {
    println "$name=${config.get(name)}"
  }

  def set(name, value) {
    config.put(name, value)
    new ConfigLoader().save(config)
  }

  def remove(name) {
    config.remove(name)
    new ConfigLoader().save(config)
  }

  def list() {
    def writer = new StringWriter()
    config.writeTo(writer)
    print writer.toString()
  }
}

