package hilltop

import hilltop.cli.*
import hilltop.commands.*

class App {
  def App(String... args) {
    def out
    if (args.contains("--json")) {
      out = new Out(new JsonWriter())
      args = args.findAll { it != "--json" }
    }
    else {
      out = new Out(new ConsoleWriter())
    }

    new Cli('hilltop', {
      describe 'An Anthill command-line utility'

      command('version', 'Get this Hilltop version') {
        execute {
          println "hilltop version 0.1"
        }
      }

      command('config', 'Manage configuration values') {
        def handler = new ConfigCommands()

        command('get', 'Get a configuration value') {
          arguments exactly: 1
          execute { opt, arguments -> handler.get(arguments[0]) }
        }

        command('set', 'Set one or more configuration values; <property=value> ...') {
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
        def handler = new ProjectCommands(out)

        command('list', 'List Anthill projects') {
          options {
            f longOpt: 'folder', args: 1, 'List Anthill projects in a specific folder'
            i longOpt: 'inactive', 'Include inactive projects'
          }
          execute { opt ->
            handler.list(opt.inactive, opt.folder)
          }
        }

        command('find', 'Find Anthill projects') {
          arguments exactly: 1, name: 'name'
          options {
            i longOpt: 'inactive', 'Include inactive projects'
          }
          execute { opt, arguments ->
            handler.find(opt.inactive, arguments[0])
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

        command('copy', 'Copy an Anthill project') {
          arguments exactly: 2, name1: 'project', name2: 'new-name'
          execute { opt, arguments ->
            handler.copy(arguments[0], arguments[1])
          }
        }
      }

      command('workflow', 'Working with Anthill workflows') {
        def handler = new WorkflowCommands(out)

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
        
        command('copy', 'Copy an Anthill workflow') {
          arguments exactly: 3, name1: 'project', name2: 'workflow', name3: 'new-name'
          execute { opt, arguments ->
            handler.copy(arguments[0], arguments[1], arguments[2])
          }
        }

        command('exec', 'Execute an operational workflow') {
          arguments exactly: 2, name1: 'workflow', name2: 'environment'
          execute { opt, arguments ->
            handler.execOperationalWorkflow(arguments[0], arguments[1], arguments[2..<arguments.size()])
          }
        }
      }

      command('workflow-property', 'Working with Anthill workflow properties') {
        def handler = new WorkflowPropertyCommands(out)

        command('list', 'List properties for an Anthill workflow') {
          arguments exactly: 2, name1: 'project', name2: 'workflow'
          execute { opt, arguments ->
            handler.list(arguments[0], arguments[1])
          }
        }
        
        command('add', 'Add a property to an existing workflow') {
          arguments exactly: 4, 
            name1: 'project', name2: 'workflow', name3: 'propertyKey', name4: 'propertyValue' 
          execute { opt, arguments ->
            handler.add(arguments[0], arguments[1], arguments[2], arguments[3])
          }
        }

        command('remove', 'Remove a property from an Anthill workflow') {
          arguments exactly: 3, 
            name1: 'project', name2: 'workflow', name3: 'propertyName'
          execute { opt, arguments ->
            handler.remove(arguments[0], arguments[1], arguments[2])
          }
        }
      }

      command('workflow-dependency', 'Working with Anthill workflow dependencies') {
        def handler = new WorkflowDependencyCommands(out)

        command('list', 'List dependencies for an Anthill workflow') {
          arguments exactly: 2, name1: 'project', name2: 'workflow'
          execute { opt, arguments ->
            handler.list(arguments[0], arguments[1])
          }
        }
        
        command('add', 'Add a dependency to an existing workflow') {
          arguments exactly: 6, 
            name1: 'dependent-project', name2: 'dependent-workflow', 
            name3: 'dependency-project', name4: 'dependency-workflow', 
            name5: 'artifact', name6: 'location'
          execute { opt, arguments ->
            handler.add(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5])
          }
        }

        command('remove', 'Remove a dependency from an Anthill workflow') {
          arguments exactly: 4, 
            name1: 'dependent-project', name2: 'dependent-workflow', 
            name3: 'dependency-project', name4: 'dependency-workflow'
          execute { opt, arguments ->
            handler.remove(arguments[0], arguments[1], arguments[2], arguments[3])
          }
        }

        command('set-conflict-strategy', 'Set dependency conflict strategy for Anthill workflow') {
          arguments exactly: 3, 
            name1: 'project', name2: 'workflow', name3: 'conflict-strategy'
          execute { opt, arguments ->
            handler.setConflictStrategy(arguments[0], arguments[1], arguments[2])
          }
        }

        command('set-trigger', 'Set dependency trigger for Anthill workflow') {
          arguments exactly: 5, 
            name1: 'dependent-project', name2: 'dependent-workflow', 
            name3: 'dependency-project', name4: 'dependency-workflow'
            name5: 'trigger'
          execute { opt, arguments ->
            handler.setTrigger(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4])
          }
        }
      }

      command('source-config', 'Working with Anthill workflow source configuration') {
        def handler = new WorkflowSourceCommands(out)

        command('set', 'Set one or more source configuration properties; <property=value> ...') {
          arguments minimum: 4, name1: 'project', name2: 'workflow', name3: 'source-type'
          execute { opt, arguments ->
            handler.setSourceConfig(arguments[0], arguments[1], arguments[2], arguments[3..<arguments.size()])
          }
        }
      }

      command('folder', 'Working with Anthill folders') {
        def handler = new FolderCommands(out)

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
        def handler = new BuildCommands(out)

        command('new', 'Request a new Anthill buildlife') {
          arguments minimum: 2, name1: 'project', name2: 'workflow'
          options {
            o longOpt: 'open', 'Open the buildlife when ready'
            _ longOpt: 'properties-file', args: 1, 'Provide build properties from a file'
          }
          execute { opt, arguments ->
            handler.start(arguments[0], arguments[1],
              opt.open, opt['properties-file'], arguments[2..<arguments.size()])
          }
        }

        command('run', 'Run a workflow against an Anthill buildlife') {
          arguments exactly: 3, name1: 'buldlife', name2: 'workflow', name3: 'environment'
          options {
            o longOpt: 'open', 'Open the buildlife when ready'
          }
          execute { opt, arguments ->
            handler.run(arguments[0], arguments[1], arguments[2], opt.open, arguments[3..<arguments.size()])
          }
        }

        command('show', 'Show details of an Anthill buildlife') {
          arguments exactly: 1, name: 'buildlife'
          execute { opt, arguments ->
            handler.show(arguments[0])
          }
        }

        command('latest', 'Get the latest build life for a project') {
          arguments minimum: 2, 
            name1: 'project', name2: 'workflow', name3: 'status'
          execute { opt, arguments ->
            handler.latest(arguments[0], arguments[1], arguments[2])
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

      command('build-link', 'Working with Anthill build links') {
        def handler = new BuildLinkCommands(out)

        command('list', 'List build links for an Anthill build life') {
          arguments exactly: 1, name: 'buildlife'
          execute { opt, arguments ->
            handler.list(arguments[0])
          }
        }

        command('add', 'Add a link to an Anthill buildlife') {
          arguments exactly: 3, name1: 'buildlife', name2: 'url', name3: 'name'
          execute { opt, arguments ->
            handler.add(arguments[0], arguments[1], arguments[2])
          }
        }

        command('open', 'Open a build link in the browser') {
          arguments exactly: 2, name1: 'buildlife', name2: 'link-name'
          execute { opt, arguments ->
            handler.open(arguments[0], arguments[1])
          }
        }
      }

      command('build-request', 'Working with Anthill build requests') {
        def handler = new BuildRequestCommands(out)

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

        command('recent', 'Show recent build requests') {
          arguments exactly: 1, name: 'project'
          execute { opt, arguments ->
            handler.recent(arguments[0])
          }
        }
      }

      command('environment', 'Working with Anthill environments') {
        def handler = new EnvironmentCommands(out)

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
        def handler = new AgentCommands(out)

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
        def handler = new LifecycleCommands(out)

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

      command('colony', 'Working with Anthill colony files') {
        def handler = new ColonyCommands()

        command('init', 'Create a new Colonyfile') {
          execute { handler.init() }
        }

        command('exec', 'Execute a Colonyfile') {
          options {
            n longOpt: 'noop', 'Execute Colonyfile without commiting changes'
          }
          execute { opt -> handler.exec(opt.noop) }
        }
      }

    }).run(args)
    out.flush()
  }

  static void main(String... args) {
    new App(args)
  }
}
