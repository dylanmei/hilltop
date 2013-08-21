package hilltop.finders

class Callbacks {
  def callback(handler) {
    new CallbackBuilder(handler).callback
  }

  class CallbackBuilder {
    def callback = new Callback()

    def CallbackBuilder(Closure handler) {
      handler.delegate = this
      handler()
    }

    def alert(actions) {
      callback.alert = actions
      callback
    }

    def error(actions) {
      callback.error = actions
      callback
    }
  }

  class Callback {
    def alert
    def error
  }
}