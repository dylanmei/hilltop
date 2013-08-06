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

  def set(properties) {
    properties.each {
      def property, value = ''
      def matcher = (it =~ /([^\s=]+)=(.*)/)
      if (matcher.matches())
        (property, value) = matcher[0].tail()
      if (property) {
        config.put(property, value)
        println "Configuration value <${property}> has been set"
      }
    }
    new ConfigLoader().save(config)
  }

  def remove(properties) {
    def map = config.flatten()
    properties.each {
      if (!map.containsKey(it))
        println "No such configuration value <${it}>"
      else {
        map.remove(it)
        config.clear()
        println "Configuration value <${it}> has been removed"
      }
    }
    config.putAll(map)
    new ConfigLoader().save(config)
  }

  def list() {
    def writer = new StringWriter()
    config.writeTo(writer)
    print writer.toString()
  }
}

