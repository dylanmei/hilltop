package hilltop

class App {
  def App(String... args) {
    cli = new Cli('hilltop', {
      description 'Anthill command-line utility'
      options {
        v longOpt: 'version', 'Provides the current hilltop version'
      },
      exec { params ->
        if (params.v) terminate 'Version 0.1'
      }

//      command 'project' {
//        description 'things to do with projects'
//        options {
//          c longOpt: 'active', 'list active projects',
//          p longOpt: 'inactive', 'list inactive projects'
//        }
//        execute { params ->
//          app.list(params.c, params.p)
//        }
//      }
    }).run(args)
  }
}
