package hilltop

import hilltop.cli.*
import hilltop.commands.*

class App {
  static def out = new Out()

  def App(String... args) {

    new Cli('hilltop', {
      describe 'An Anthill command-line utility'

      command('version', 'Get this Hilltop version') {
        execute {
          out.echo('version', 'hilltop version: 0.1')
        }
      }

      command('config', 'Manage configuration values') {
        def handler = new ConfigCommands()

        command('get', 'Get a configuration value') {
          arguments exactly: 1
          execute { opt, arguments -> handler.get(arguments[0]) }
        }

        command('set', 'Set configuration values; <property=value> ...') {
          arguments minimum: 1
          execute { opt, arguments -> handler.set(arguments) }
        }

        command('remove', 'Remove configuration values') {
          arguments minimum: 1
          execute { opt, arguments -> handler.remove(arguments) }
        }

        command('show', 'Show the current configuration values') {
          execute { handler.show() }
        }
      }

      command('project', 'Working with Anthill projects') {
        def handler = new ProjectCommands()

        command('list', 'List Anthill projects') {
          options {
            f longOpt: 'folder', args: 1, 'List Anthill projects in a specific folder'
            i longOpt: 'inactive', 'Include inactive projects'
            l longOpt: 'like', args: 1, 'Project name to match'
          }
          execute { opt ->
            handler.list(opt.inactive, opt.folder, opt.like)
          }
        }

        command('show', 'Show details of an Anthill project') {
          arguments exactly: 1, name: 'project'
          execute { opt, arguments ->
            handler.show(arguments[0])
          }
        }

        command('open', 'Open an Anthill project in the browser') {
          arguments exactly: 1, name: 'project'
          options {
            a longOpt: 'admin', 'Open the administrative configuration page'
          }
          execute { opt, arguments ->
            handler.open(arguments[0], opt.admin)
          }
        }

        command('remove', 'Remove an Anthill project') {
          arguments exactly: 1, name: 'project'
          execute { opt, arguments ->
            handler.remove(arguments[0])
          }
        }
      }

      command('workflow', 'Working with Anthill workflows') {
        def handler = new WorkflowCommands()

        command('list', 'List Anthill workflows in a project') {
          arguments exactly: 1, name: 'project'
          options {
            i longOpt: 'inactive', 'Include inactive workflows'
          }
          execute { opt, arguments ->
            handler.list(arguments[0], opt.inactive)
          }
        }

        command('show', 'Show details of an Anthill workflow') {
          arguments exactly: 2, name1: 'project', name2: 'workflow'
          execute { opt, arguments ->
            handler.show(arguments[0], arguments[1])
          }
        }

        command('open', 'Open an Anthill workflow in the browser') {
          arguments exactly: 2, name1: 'project', name2: 'workflow'
          options {
            a longOpt: 'admin', 'Open the administrative configuration page'
          }
          execute { opt, arguments ->
            handler.open(arguments[0], arguments[1], opt.admin)
          }
        }

        command('remove', 'Remove an Anthill workflow') {
          arguments exactly: 2, name1: 'project', name2: 'workflow'
          options {
            n longOpt: 'noop', 'Remove Workflow without commiting changes'
            f longOpt: 'force', 'Remove Workflow at all costs'
          }
          execute { opt, arguments ->
            handler.remove(arguments[0], arguments[1], opt.force, opt.noop)
          }
        }
      }

      command('folder', 'Working with Anthill folders') {
        def handler = new FolderCommands()

        command('list', 'List Anthill folders') {
          options {
            i longOpt: 'inactive', 'Include inactive folders'
          }
          execute { opt ->
            handler.list(opt.inactive)
          }
        }

        command('show', 'Show details of an Anthill folder') {
          arguments exactly: 1, name: 'folder'
          execute { opt, arguments ->
            handler.show(arguments[0])
          }
        }
      }

      command('build', 'Working with Anthill builds') {
        def handler = new BuildCommands()

        command('new', 'Request a new Anthill buildlife') {
          arguments exactly: 2, name1: 'project', name2: 'workflow'
          options {
            o longOpt: 'open', 'Open the buildlife when ready'
          }
          execute { opt, arguments ->
            handler.start(arguments[0], arguments[1], opt.open)
          }
        }

        command('run', 'Run a workflow against an Anthill buildlife') {
          arguments exactly: 3, name1: 'buldlife', name2: 'workflow', name3: 'environment'
          options {
            o longOpt: 'open', 'Open the buildlife when ready'
          }
          execute { opt, arguments ->
            handler.run(arguments[0], arguments[1], arguments[2], opt.open)
          }
        }

        command('show', 'Show details of an Anthill buildlife') {
          arguments exactly: 1, name: 'buildlife'
          execute { opt, arguments ->
            handler.show(arguments[0])
          }
        }

        command('open', 'Open an Anthill buildlife in the browser') {
          arguments exactly: 1, name: 'buildlife'
          execute { opt, arguments ->
            handler.open(arguments[0])
          }
        }

        command('remove', 'Remove an Anthill buildlife') {
          arguments exactly: 1, name: 'buildlife'
          execute { opt, arguments ->
            handler.remove(arguments[0])
          }
        }
      }

      command('request', 'Working with Anthill build requests') {
        def handler = new RequestCommands()

        command('show', 'Show details of an Anthill build request') {
          arguments exactly: 1, name: 'request'
          execute { opt, arguments ->
            handler.show(arguments[0])
          }
        }

        command('open', 'Open an Anthill build request in the browser') {
          arguments exactly: 1, name: 'request'
          execute { opt, arguments ->
            handler.open(arguments[0])
          }
        }
      }

      command('environment', 'Working with Anthill environments') {
        def handler = new EnvironmentCommands()

        command('list', 'List Anthill environments') {
          options {
            f longOpt: 'group', args: 1, 'List Anthill environments in a specific environment group'
          }
          execute { opt ->
            handler.list(opt.group)
          }
        }

        command('show', 'Show details of an Anthill environment') {
          arguments exactly: 1, name: 'environment'
          execute { opt, arguments ->
            handler.show(arguments[0])
          }
        }

        command('open', 'Open an Anthill environment in the browser') {
          arguments exactly: 1, name: 'environment'
          execute { opt, arguments ->
            handler.open(arguments[0])
          }
        }
      }

      command('agent', 'Working with Anthill agents') {
        def handler = new AgentCommands()

        command('list', 'List Anthill agents') {
          execute { handler.list() }
        }

        command('show', 'Show details of an Anthill agent') {
          arguments exactly: 1, name: 'agent'
          execute { opt, arguments ->
            handler.show(arguments[0])
          }
        }

        command('open', 'Open an Anthill agent in the browser') {
          arguments exactly: 1, name: 'agent'
          execute { opt, arguments ->
            handler.open(arguments[0])
          }
        }
      }

      command('lifecycle', 'Working with Anthill lifecycles') {
        def handler = new LifecycleCommands()

        command('list', 'List Anthill lifecycles') {
          execute { handler.list() }
        }

        command('show', 'Show details of an Anthill lifecycle') {
          arguments exactly: 1, name: 'lifecycle'
          execute { opt, arguments ->
            handler.show(arguments[0])
          }
        }

        command('open', 'Open an Anthill lifecycle in the browser') {
          arguments exactly: 1, name: 'lifecycle'
          execute { opt, arguments ->
            handler.open(arguments[0])
          }
        }
      }

      // command('colony', 'Working with Anthill colony files') {
      //   def handler = new ColonyCommands()

      //   command('init', 'Create a new Colonyfile') {
      //     execute { handler.init() }
      //   }

      //   command('exec', 'Execute a Colonyfile') {
      //     options {
      //       n longOpt: 'noop', 'Execute Colonyfile without commiting changes'
      //     }
      //     execute { opt -> handler.exec(opt.noop) }
      //   }
      // }

    }).run(args)
    out.flush()
  }

  static void main(String... args) {
    new App(args)
  }
}
