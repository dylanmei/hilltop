package hilltop

import com.urbancode.anthill3.main.client.AnthillClient;
import com.urbancode.anthill3.persistence.UnitOfWork;
import com.urbancode.anthill3.domain.folder.*;
import com.urbancode.anthill3.domain.project.*;

class ProjectCommands {
  def config

  def ProjectCommands(config) {
    this.config = config
  }

  def folder(name) {
    def token = config.get('token')
    def server = config.get('server')

    def client = AnthillClient.connect(server, 4567, token)
    UnitOfWork uow = client.createUnitOfWork();

    def folder = FolderFactory.getInstance().restoreForName(name)
    if (!folder)
      println "No such folder <${name}>"
    else {
      folder.getProjects()
        .findAll { f -> f.isActive }
        .each { p -> println "${p.getName()}" }
    }

    uow.commitAndClose();
  }
}