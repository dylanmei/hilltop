package hilltop

import hilltop.cli.*
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
            i longOpt: 'inactive', 'Include inactive projects'
          }
          execute { p ->
            handler.list(p.inactive, p.folder)
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
          describe 'Open an Anthill project in the browser'
          arguments exactly: 1, name: 'project'
          options {
            a longOpt: 'admin', 'Open the administrative configuration page'
          }
          execute { p ->
            handler.open(p.arguments().first(), p.a)
          }
        }

        command('remove') {
          describe 'Remove an Anthill project'
          arguments exactly: 1, name: 'project'
          execute { p ->
            handler.remove(p.arguments()[0])
          }
        }
      }

      command('workflow') {
        def handler = new WorkflowCommands()
        describe 'Working with Anthill workflows'

        command('list') {
          describe 'List Anthill workflows in a project'
          arguments exactly: 1, name: 'project'
          options {
            i longOpt: 'inactive', 'Include inactive workflows'
          }
          execute { p -> handler.list(p.arguments()[0], p.inactive) }
        }

        command('show') {
          describe 'Show details of an Anthill workflow'
          arguments exactly: 2, name1: 'project', name2: 'workflow'
          execute { p ->
            handler.show(p.arguments()[0], p.arguments()[1])
          }
        }

        command('open') {
          describe 'Open an Anthill workflow in the browser'
          arguments exactly: 2, name1: 'project', name2: 'workflow'
          options {
            a longOpt: 'admin', 'Open the administrative configuration page'
          }
          execute { p ->
            handler.open(p.arguments()[0], p.arguments()[1], p.a)
          }
        }

        command('remove') {
          describe 'Remove an Anthill workflow'
          arguments exactly: 2, name1: 'project', name2: 'workflow'
          execute { p ->
            handler.remove(p.arguments()[0], p.arguments()[1])
          }
        }
      }

      command('folder') {
        def handler = new FolderCommands()
        describe 'Working with Anthill folders'

        command('list') {
          describe 'List Anthill folders'
          options {
            i longOpt: 'inactive', 'Include inactive folders'
          }
          execute { p -> handler.list(p.inactive) }
        }
        
        command('show') {
          describe 'Show details of an Anthill folder'
          arguments exactly: 1, name: 'folder'
          execute { p -> handler.show(p.arguments()[0]) }
        }        
      }

      command('build') {
        def handler = new BuildCommands()
        describe 'Working with Anthill builds'

        command('new') {
          describe 'Request a new Anthill buildlife'
          arguments exactly: 2, name1: 'project', name2: 'workflow'
          options {
            o longOpt: 'open', 'Open the buildlife when ready'
          }
          execute { p ->
            handler.start(p.arguments()[0], p.arguments()[1], p.open)
          }
        }

        command('run') {
          describe 'Run a workflow against an Anthill buildlife'
          arguments exactly: 3, name1: 'buldlife', name2: 'workflow', name3: 'environment'
          options {
            o longOpt: 'open', 'Open the buildlife when ready'
          }
          execute { p ->
            handler.run(p.arguments()[0], p.arguments()[1], p.arguments()[2], p.open)
          }
        }

        command('show') {
          describe 'Show details of an Anthill buildlife'
          arguments exactly: 1, name: 'buildlife'
          execute { p -> handler.show(p.arguments()[0]) }
        }

        command('open') {
          describe 'Open an Anthill buildlife in the browser'
          arguments exactly: 1, name: 'buildlife'
          execute { p -> handler.open(p.arguments()[0]) }
        }

        command('remove') {
          describe 'Remove an Anthill buildlife'
          arguments exactly: 1, name: 'buildlife'
          execute { p ->
            handler.remove(p.arguments()[0])
          }
        }
      }

      command('request') {
        def handler = new RequestCommands()
        describe 'Working with Anthill build requests'

        command('show') {
          describe 'Show details of an Anthill build request'
          arguments exactly: 1, name: 'request'
          execute { p -> handler.show(p.arguments()[0]) }
        }

        command('open') {
          describe 'Open an Anthill build request in the browser'
          arguments exactly: 1, name: 'request'
          execute { p -> handler.open(p.arguments()[0]) }
        }
      }

      command('environment') {
        def handler = new EnvironmentCommands()
        describe 'Working with Anthill environments'

        command('list') {
          describe 'List Anthill environments'
          options {
            f longOpt: 'group', args: 1, 'List Anthill environments in a specific environment group'
          }
          execute { p -> handler.list(p.group) }
        }

        command('show') {
          describe 'Show details of an Anthill environment'
          arguments exactly: 1, name: 'environment'
          execute { p -> handler.show(p.arguments()[0]) }
        }

        command('open') {
          describe 'Open an Anthill environment in the browser'
          arguments exactly: 1, name: 'environment'
          execute { p -> handler.open(p.arguments()[0]) }
        }
      }

      command('lifecycle') {
        def handler = new LifecycleCommands()
        describe 'Working with Anthill lifecycles'

        command('list') {
          describe 'List Anthill lifecycles'
          execute { handler.list() }
        }

        command('show') {
          describe 'Show details of an Anthill lifecycle'
          arguments exactly: 1, name: 'lifecycle'
          execute { p -> handler.show(p.arguments()[0]) }
        }

        command('open') {
          describe 'Open an Anthill lifecycle in the browser'
          arguments exactly: 1, name: 'lifecycle'
          execute { p -> handler.open(p.arguments()[0]) }
        }
      }

      command('colony') {
        def handler = new ColonyCommands()
        describe 'Working with Anthill colony files'

        command('init') {
          describe 'Create a new Colonyfile'
          execute { handler.init() }
        }

        command('exec') {
          describe 'Execute a Colonyfile'
          options {
            n longOpt: 'noop', 'Execute Colonyfile without commiting changes'
          }
          execute { p -> handler.exec(p.noop) }
        }
      }

    }).run(args)
  }
}
