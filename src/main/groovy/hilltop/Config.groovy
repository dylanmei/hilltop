package hilltop

class Config {
  def static final HOME = System.getProperty('user.home')
  def static final FILE = new File(HOME, ".hilltop").path

  @Lazy obj = new ConfigLoader().load()

  private final class ConfigLoader {

    def load() {
      def file = new File(FILE)
      file.exists() ?
        new ConfigSlurper().parse(file.toURL()) : new ConfigObject()
    }

    def save(config) {
      def file = new File(FILE)
      file.withWriter{ writer ->
        config.writeTo(writer)
      }
    }
  }

  def get(key) {
    obj.get(key)
  }

  def put(key, value) {
    obj.put(key, value)
  }

  def remove(key) {
    def map = obj.flatten()
    map.remove(key)
    obj.clear()
    obj.putAll(map)
  }

  def save() {
    new ConfigLoader().save(obj)
  }

  def writeTo(writer) {
    obj.writeTo(writer)
  }
}