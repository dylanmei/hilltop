package hilltop

class Settings {
  def config

  def Settings(config) {
    this.config = config ?: new Config()
  }

  def getProperty(String propertyName) {
    switch (propertyName) {
      case "anthill":
        return new SubSettings(propertyName, config)
    }
    metaClass.getProperty(propertyName)
  }
}

class SubSettings {
  def settingName
  def config

  def SubSettings(String name, config) {
    this.settingName = name
    this.config = config
  }

  def getProperty(String propertyName) {
    getEnv(propertyName) ?: getConf(propertyName)
  }

  def getEnv(String propertyName) {
    def value = System.getenv("HILLTOP_${this.settingName}_${propertyName}".toUpperCase())
    return value == "" ? null : value
  }

  def getConf(String propertyName) {
    def obj = config.get(this.settingName)
    obj == null ? "" : (obj[propertyName] ?: "")
  }
}
