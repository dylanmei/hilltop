package hilltop.commands

import hilltop.ConfigLoader

@Mixin(ConsoleCommands)
class ConfigCommands {
  def config

  def ConfigCommands(config) {
  	this.config = config
  }

  def get(name) {
    def value = name.tokenize('.')
      .inject(config) { c, key -> c[key] }
    echo "$name=${value}"
  }

  def set(properties) {
    properties.each {
      def property, value = ''
      def matcher = (it =~ /([^\s=]+)=(.*)/)
      if (matcher.matches())
        (property, value) = matcher[0].tail()
      if (property) {
        config.put(property, value)
        echo "Configuration value <${property}> has been set"
      }
    }

    new ConfigLoader().save(config)
  }

  def remove(properties) {
    def map = config.flatten()
    properties.each {
      if (!map.containsKey(it))
        echo "No such configuration value <${it}>"
      else {
        map.remove(it)
        config.clear()
        echo "Configuration value <${it}> has been removed"
      }
    }
    config.putAll(map)
    new ConfigLoader().save(config)
  }

  def list() {
    def writer = new StringWriter()
    config.writeTo(writer)
    echo writer.toString()
  }
}
