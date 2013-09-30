
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
  def setup() {
    GroovyMock(AnthillEngine, global: true)
  }

  def 'run a workflow against an environment'() {
    setup:
    def environment = create_environment(name: 'Dev')
    def workflow = create_workflow(name: 'Deploy', environment: environment)

    when:
    new WorkflowRunner(create_buildlife(workflow: workflow))
      .request('Deploy', 'Dev', [:])

    then:
    1 * AnthillEngine.create_workflow_request(_, workflow, environment)
  }

  def 'run a workflow without an environment'() {
    setup:
    def environment = create_environment(name: 'Dev')
    def workflow = create_workflow(name: 'Deploy', environment: environment)

    when: 'the environment name does not match'
    new WorkflowRunner(create_buildlife(workflow: workflow))
      .request('Deploy', 'Beta', [:])

    then: 'an error is raised'
    thrown(GroovyRuntimeException)
  }

  def 'run a workflow with a partial workflow name match'() {
    setup:
    def environment = create_environment(name: 'Dev')
    def workflow = create_workflow(name: 'Deploy to lower environment', environment: environment)

    when:
    new WorkflowRunner(create_buildlife(workflow: workflow))
      .request('Deploy to', 'Dev', [:])

    then:
    1 * AnthillEngine.create_workflow_request(_, workflow, environment)
  }

  def 'run a workflow with a partial environment name match'() {
    setup:
    def environment = create_environment(name: 'Development')
    def workflow = create_workflow(name: 'Deploy', environment: environment)

    when:
    new WorkflowRunner(create_buildlife(workflow: workflow))
      .request('Deploy', 'Dev', [:])

    then:
    1 * AnthillEngine.create_workflow_request(_, workflow, environment)
  }

  def create_workflow(map) {
    def name = map['name'] ?: ''
    def active = map['active'] ?: true

    def environments = map['environments'] ?: []
    if (map['environment'])
      environments += map['environment']

    Mock(Workflow) {
      getName() >> name
      isActive() >> active
      getServerGroupArray() >> environments
    }
  }

  def create_environment(map) {
    def name = map['name'] ?: ''
    Mock(ServerGroup) {
      getName() >> name
    }    
  }

  def create_buildlife(map) {
    def workflow = map['workflow']
    def project = Mock(Project)
    project.getWorkflowArray() >> [workflow]

    Mock(BuildLife) {
      getProject() >> project
    }
  }
}



