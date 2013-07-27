
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

    command('sub-command') {
      options {
        x 'test sub-command x'
      }
      execute { params ->
        if (params.x)
          writer << 'executing sub-command x'
      }
    }
  }

  def 'core command with simple option is executed'() {
    def cli = new Cli('test', config)
    writer = new StringWriter()

    when:
      cli.run('-v')
    then:
      writer.toString() == 'version 1'
  }

  def 'core command with argument option is executed'() {
    def cli = new Cli('test', config)
    writer = new StringWriter()

    when:
      cli.run('-e', 'Hello World')
    then:
      writer.toString() == 'Hello World'
  }

  def 'sub command with simple option is executed'() {
    def cli = new Cli('test', config)
    writer = new StringWriter()

    when:
      cli.run('sub-command', '-x')
    then:
      writer.toString() == 'executing sub-command x'
  }
}