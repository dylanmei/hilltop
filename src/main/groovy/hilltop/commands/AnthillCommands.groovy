package hilltop.commands

import hilltop.Config

@Mixin(AnthillHelper)
@Mixin(ConsoleHelper)
class AnthillCommands {
  def config = new Config()

  def <T> T Finder(Class<T> type) {
    type.newInstance({
      alert { m -> echo m }
      error { m -> quit m }
    })
  }

  def browse(url) {
    Class<?> d = Class.forName("java.awt.Desktop");
    d.getDeclaredMethod("browse", [java.net.URI.class] as Class[]).invoke(
    d.getDeclaredMethod("getDesktop").invoke(null), [java.net.URI.create(url)] as Object[]);
  }  
}
