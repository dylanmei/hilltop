package hilltop

import hilltop.cli.Cli
import hilltop.commands.*

class App {
  def App(String... args) {

    new Cli('hilltop', {
      describe 'An Anthill command-line utility'
      options {
        v longOpt: 'version', 'Gets the current Hilltop version'
      }
      execute { params ->
        if (params.v) {
          println 'hilltop version: 0.1'; System.exit(0)
        }
      }

      command('config') {
        def handler = new ConfigCommands()
        describe 'Manage configuration values'

        command('get') {
          describe 'Get a configuration value'
          arguments exactly: 1
          execute { p -> handler.get(p.arguments().first()) }
        }

        command('set') {
          describe 'Set configuration values; <property=value> ...'
          arguments minimum: 1
          execute { p -> handler.set(p.arguments()) }
        }

        command('remove') {
          describe 'Remove configuration values'
          arguments minimum: 1
          execute { p -> handler.remove(p.arguments()) }
        }

        command('list') {
          describe 'List the current configuration values'
          execute { handler.list() }
        }
      }

      command('project') {
        def handler = new ProjectCommands()
        describe 'Working with Anthill projects'

        command('list') {
          describe 'List Anthill projects'
          options {
            f longOpt: 'folder', args: 1, 'List Anthill projects in a specific folder'
            i longOpt: 'inactive', 'List inactive projects'
          }
          execute { p ->
            if (!p.f) handler.list(p.i)
            else handler.list_in_folder(p.f, p.i)
          }
        }

        command('show') {
          describe 'Show details of an Anthill project'
          arguments exactly: 1, name: 'project'
          execute { p ->
            handler.show(p.arguments().first())
          }
        }

        command('open') {
          describe 'Launch an Anthill project in the browser'
          arguments exactly: 1, name: 'project'
          options {
            a longOpt: 'admin', 'Launch the administrative configuration page'
          }
          execute { p ->
            handler.open(p.arguments().first(), p.a)
          }
        }
      }

      command('workflow') {
        def handler = new WorkflowCommands()
        describe 'Working with Anthill workflows'

        command('show') {
          describe 'Show details of an Anthill workflow'
          arguments exactly: 2, name1: 'project', name2: 'workflow'
          execute { p ->
            handler.show(p.arguments()[0], p.arguments()[1])
          }
        }

        command('open') {
          describe 'Launch an Anthill workflow in the browser'
          arguments exactly: 2, name1: 'project', name2: 'workflow'
          options {
            a longOpt: 'admin', 'Launch the administrative configuration page'
          }
          execute { p ->
            handler.open(p.arguments()[0], p.arguments()[1], p.a)
          }
        }
      }

      command('build') {
        def handler = new BuildCommands()
        describe 'Working with Anthill builds'

        command('start') {
          describe 'Request a new Anthill Buildlife'
          arguments exactly: 2, name1: 'project', name2: 'workflow'
          options {
            o longOpt: 'open', 'Launch the Buildlife when ready'
          }
          execute { p ->
            handler.start(p.arguments()[0], p.arguments()[1], p.open)
          }
        }

        command('open') {
          describe 'Launch an Anthill Buildlife in the browser'
          arguments exactly: 1, name: 'buildlife'
          execute { p -> handler.open(p.arguments()[0]) }
        }
      }

      command('environment') {
        def handler = new EnvironmentCommands()
        describe 'Working with Anthill environments'

        command('list') {
          describe 'List Anthill environments'
          execute { handler.list() }
        }

        command('show') {
          describe 'Show details of an Anthill environment'
          arguments exactly: 1, name: 'environment'
          execute { p -> handler.show(p.arguments()[0]) }
        }

        command('open') {
          describe 'Launch an Anthill environment in the browser'
          arguments exactly: 1, name: 'environment'
          execute { p -> handler.open(p.arguments()[0]) }
        }
      }
    }).run(args)
  }
}
