
package hilltop

import spock.lang.*

class CliSpec extends Specification {
  def writer
	def config = {
    describe 'test description'
    options {
      v longOpt: 'version', 'test version'
      e longOpt: 'echo', args: 1, argName: 'message', 'test echo'
    }
    execute { params ->
      if (params.v)
        writer << 'version 1'
      if (params.e)
        writer << params.e
    }

    command('child') {
      options {}
      execute { params ->
        writer << 'executing child'
      }

      command('grand-child') {
        options {}
        execute { params ->
          writer << 'executing grand-child'
        }
      }
    }
  }

  def 'command with simple option is executed'() {
    def cli = new Cli('test', config)
    writer = new StringWriter()

    when:
      cli.run('-v')
    then:
      writer.toString() == 'version 1'
  }

  def 'command with argument option is executed'() {
    def cli = new Cli('test', config)
    writer = new StringWriter()

    when:
      cli.run('-e', 'Hello World')
    then:
      writer.toString() == 'Hello World'
  }

  def 'child command is executed'() {
    def cli = new Cli('test', config)
    writer = new StringWriter()

    when:
      cli.run('child')
    then:
      writer.toString() == 'executing child'
  }

  def 'grand-child command is executed'() {
    def cli = new Cli('test', config)
    writer = new StringWriter()

    when:
      cli.run('child', 'grand-child')
    then:
      writer.toString().contains('executing child')
      writer.toString().contains('executing grand-child')
  }
}