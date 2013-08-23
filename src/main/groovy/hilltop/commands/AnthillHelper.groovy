package hilltop.commands

import com.urbancode.anthill3.main.client.AnthillClient;
import com.urbancode.anthill3.persistence.UnitOfWork;

class AnthillHelper {
  protected work(Closure task) {
    def settings = config.get('anthill')
    if (settings == null || settings.api_token.isEmpty() || settings.api_server.isEmpty()) {
      quit 'Your Anthill configuration requires anthill.api_server and anthill.api_token values.'
    }

    def result
    def client = AnthillClient.connect(settings.api_server, 4567, settings.api_token)
    def uow = client.createUnitOfWork()
    try {
      result = task(uow)
    }
    catch (Exception e) {
      if (!uow.isClosed())
        uow.cancel()
      throw e
    }

    uow.commitAndClose();
    result
  }
}
