package hilltop.commands

import hilltop.Config

@Mixin(ConsoleHelper)
class ConfigCommands {
  def config = new Config()

  def get(name) {
    def value = name.tokenize('.')
      .inject(config) { c, key -> c.get(key) }
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

    config.save()
  }

  def remove(properties) {
    properties.each {
      if (!exists(it))
        echo "No such configuration value <${it}>"
      else {
        config.remove(it)
        echo "Configuration value <${it}> has been removed"
      }
    }

    config.save()
  }

  def list() {
    def writer = new StringWriter()
    config.writeTo(writer)
    def content = writer.toString()
    if (!content) echo "[Empty]"
    else {
      echo "[${Config.FILE}]"
      echo writer.toString()
    }
  }

  private Boolean exists(name) {
    null != name.tokenize('.')
      .inject(config) { c, key -> c.get(key) }
  }
}
