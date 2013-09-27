
package hilltop

import spock.lang.*
import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.domain.buildrequest.*
import com.urbancode.anthill3.domain.buildlife.*
import com.urbancode.anthill3.domain.servergroup.*
import com.urbancode.anthill3.persistence.UnitOfWork

import hilltop.runners.*

class WorkflowRunnerSpec extends Specification {

  def 'running a workflow'() {
    given: "a 'Deploy' workflow and a 'Dev' environment"
      GroovyMock(AnthillEngine, global: true)

      def b = Mock(BuildLife)
      def p = Mock(Project)
      b.getProject() >> p

      def w = Mock(Workflow)
      w.getName() >> 'Deploy'
      w.isOriginating() >> false
      w.isActive() >> true
      p.getWorkflowArray() >> [w]

      def e = Mock(ServerGroup)
      e.getName() >> 'Dev'
      w.getServerGroupArray() >> [e]


    when: "running 'Deploy' to 'Dev'"
      new WorkflowRunner(b).request('Deploy', 'Dev', [:])

    then: "a build request is created"
      1 * AnthillEngine.create_workflow_request(b, w, e)
  }
}