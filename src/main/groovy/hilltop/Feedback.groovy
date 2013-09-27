package hilltop

class Feedback {
  Closure callbackHandler

  protected void init_feedback(Closure handlers) {
    this.callbackHandler = handlers
  }

  protected void alert(message) {
    if (callbackHandler)
      new CallbackBuilder(callbackHandler)
        .callback.alert(message)
  }

  protected void error(message) {
    if (callbackHandler) {
      new CallbackBuilder(callbackHandler)
        .callback.error(message)
    }
    else {
      throw new GroovyRuntimeException(message)
    }
  }

  class Callback {
    def alert
    def error
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
}
