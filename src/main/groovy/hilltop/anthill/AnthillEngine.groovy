
package hilltop.anthill

import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.services.build.*
import com.urbancode.anthill3.domain.buildrequest.*
import com.urbancode.anthill3.domain.buildlife.*
import com.urbancode.anthill3.domain.servergroup.*
import com.urbancode.anthill3.main.client.AnthillClient;
import com.urbancode.anthill3.persistence.UnitOfWork;

class AnthillEngine {
  public static BuildRequest create_build_request(Workflow workflow, propertyMap = null) {
    def uow = workflow.unitOfWork
    def request = BuildRequest.createOriginatingRequest(
      workflow.buildProfile, uow.user, RequestSourceEnum.MANUAL, uow.user)

    if(propertyMap != null) {
      propertyMap.each { key, value ->
        request.setPropertyValue(key, value, false)
      }
    }

    request.forcedFlag = true
    request.unitOfWork = uow
    request.store()
    request
  }

  public static BuildRequest create_workflow_request(buildlife, workflow, server_group) {
    def uow = buildlife.unitOfWork
    def request = BuildRequest.createNonOriginatingRequest(
      buildlife, workflow, server_group, uow.user, RequestSourceEnum.MANUAL, uow.user)
    request.forcedFlag = true
    request.unitOfWork = uow
    request.store()
    request    
  }

  public static Object submit_work(AnthillClient client, Closure task) {
    def result
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

  public static void submit_build_request(BuildRequest request) {
    def service = new BuildServiceImplClient()
    try {
      service.init()
      service.runBuild(request)
    }
    finally {
      service.shutdown()
    }    
  }

  public static void submit_workflow_request(BuildRequest request) {
    def service = new BuildServiceImplClient()
    try {
      service.init()
      service.runWorkflow(request)
    }
    finally {
      service.shutdown()
    }
  }
}
