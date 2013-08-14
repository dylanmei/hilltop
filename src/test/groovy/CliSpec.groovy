
package hilltop

import hilltop.cli.*
import spock.lang.*

class CliSpec extends Specification {

  def 'command with simple option is executed'() {
    def writer = new StringWriter()
    def cli = new Cli('test', {
      options {
        m 'test message'
      }
      execute { params ->
        if (params.m) writer << 'hello world'
      }
    })

    when:
      cli.run('-m')
    then:
      writer.toString() == 'hello world'
  }

  def 'command with argument option is executed'() {
    def writer = new StringWriter()
    def cli = new Cli('test', {
      options {
        m args: 1, 'test message'
      }
      execute { params ->
        if (params.m) writer << 'hello ' + params.m
      }
    })

    when:
      cli.run('-m', 'hilltop')
    then:
      writer.toString() == 'hello hilltop'
  }

  def 'command with required argument is executed'() {
    def writer = new StringWriter()
    def cli = new Cli('test', {
      arguments exactly: 1
      execute { params ->
        writer << 'hello ' + params.arguments().first()
      }
    })
    when:
      cli.run('hilltop')
    then:
      writer.toString() == 'hello hilltop'
  }

  def 'command with required argument missing executes help'() {
    def writer = new StringWriter()
    def cli = new Cli('test', {
      arguments exactly: 1
    }, new PrintWriter(writer))
    when:
      cli.run()
    then:
      writer.toString().contains('usage: test')
  }

  def 'child command is executed'() {
    def writer = new StringWriter()
    def cli = new Cli('test', {
      command('child') {
        execute {
          writer << 'executing child'
        }
      }
    })

    when:
      cli.run('child')
    then:
      writer.toString() == 'executing child'
  }

  def 'grand-child command is executed'() {
    def writer = new StringWriter()
    def cli = new Cli('test', {
      command('child') {
        execute {
          writer << 'executing child'
        }
        command('grand-child') {
          execute {
            writer << 'executing grand-child'
          }
        }
      }
    })

    when:
      cli.run('child', 'grand-child')
    then:
      writer.toString().contains('executing child')
      writer.toString().contains('executing grand-child')
  }
}