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
    def propertyMap = PropertyHelper.toMap(properties)
   
    propertyMap.each { key, value -> 
      config.put(key, value)
      println "Configuration value <${key}> has been set to <${value}>"
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
