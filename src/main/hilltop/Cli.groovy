
package hilltop;


class Cli {
  def params

  Cli(params) {
    this.params = params
  }

  def command() {
    new Command('Hello World')
  }
}

class Command {
  def text

  Command(text) {
    this.text = text
  }

  def run() {
    println this.text
  }
}
