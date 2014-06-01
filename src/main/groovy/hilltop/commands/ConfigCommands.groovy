package hilltop.commands

import hilltop.Config

class ConfigCommands {
  def config = new Config()
  def get(name) {
    def value = name.tokenize('.')
      .inject(config) { c, key -> c.get(key) }
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

    config.save()
  }

  def remove(properties) {
    properties.each {
      if (!exists(it))
        println "No such configuration value <${it}>"
      else {
        config.remove(it)
        println "Configuration value <${it}> has been removed"
      }
    }

    config.save()
  }

  def show() {
    def writer = new StringWriter()
    config.writeTo(writer)
    def content = writer.toString()
    if (!content) println "[Empty]"
    else {
      println "[${Config.FILE}]"
      println writer.toString()
    }
  }

  private Boolean exists(name) {
    null != name.tokenize('.')
      .inject(config) { c, key -> c.get(key) }
  }
}
