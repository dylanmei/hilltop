package hilltop.finders

class FinderCallbacks {
  Closure callbackHandler

  def callbacks(Closure handler) {
    this.callbackHandler = handler
  }

  def alert(message) {
    if (callbackHandler)
      new CallbackBuilder(handler).callback.alert(message)
  }

  def error(message) {
    if (callbackHandler)
      new CallbackBuilder(handler).callback.error(message)
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