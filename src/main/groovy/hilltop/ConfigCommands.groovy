package hilltop

class ConfigCommands {
  def config

  def ConfigCommands(config) {
  	this.config = config
  }

  def get(name) {
    def value = name.tokenize('.')
      .inject(config) { c, key -> c[key] }
    println "$name=${value}"
  }

  def set(name, value) {
    config.put(name, value)
    new ConfigLoader().save(config)
    println "Configuration value <${name}> has been set"
  }

  def remove(name) {
    def map = config.flatten()
    if (!map.containsKey(name))
      println "No such configuration value <${name}>"
    else {
      map.remove(name)
      config.clear()
      config.putAll(map)
      new ConfigLoader().save(config)
      println "Configuration value <${name}> has been removed"
    }
  }

  def list() {
    def writer = new StringWriter()
    config.writeTo(writer)
    print writer.toString()
  }
}

