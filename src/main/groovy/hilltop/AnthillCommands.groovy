package hilltop

import com.urbancode.anthill3.main.client.AnthillClient;
import com.urbancode.anthill3.persistence.UnitOfWork;
import com.urbancode.anthill3.domain.project.*;

class AnthillCommands {
  protected get_project_or_complain(name) {
    if (name == '.') name = infer_project_name(name)

    def projects = ProjectFactory.getInstance()
      .restoreAllForName(name)

    if (projects.size() > 1)
      println "There are ${projects.size()} named <$name>; taking the first one"
    if (projects.size() == 0)
      quit "No such project <$name>"

    projects[0]
  }

  protected String infer_project_name(name) {
    if (name != '.') return name
    System.getProperty("user.dir")
      .tokenize(File.separator).last()
  }

  protected work(Closure task) {
    def settings = config.get('anthill')
    def client = AnthillClient.connect(settings.api_server, 4567, settings.api_token)
    def uow = client.createUnitOfWork()
    def result = task()
    uow.commitAndClose();
    result
  }

  def quit(message, throwable = null) {
    if (message)
      println message + '\n'
    if (throwable)
      throw throwable
    System.exit(0)
  }
}
