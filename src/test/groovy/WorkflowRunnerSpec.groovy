
package hilltop

import spock.lang.*
import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.domain.buildrequest.*
import com.urbancode.anthill3.domain.buildlife.*
import com.urbancode.anthill3.domain.servergroup.*
import com.urbancode.anthill3.persistence.UnitOfWork

import hilltop.anthill.*

/*

fixture:
  project
    master
    deploy candidate
      - load
      - beta
      - test integration
    deploy staging
      - load
      - stage
*/

class WorkflowRunnerSpec extends Specification {
  def setup() {
    GroovyMock(AnthillEngine, global: true)
  }

  def 'run an originating workflow'() {
    setup:
    def workflow = create_workflow(name: 'master', originating: true)

    when:
    new WorkflowRunner(create_buildlife(workflow: workflow))
      .request('master', 'beta', [:])
    
    then: 'an error is raised'
    thrown(GroovyRuntimeException)
  }

  def 'run an inactive workflow'() {
    setup:
    def environment = create_environment(name: 'beta')
    def workflow = create_workflow(name: 'deploy candidate', active: false, environment: environment)

    when:
    new WorkflowRunner(create_buildlife(workflow: workflow))
      .request('deploy candidate', 'beta', [:])
    
    then: 'an error is raised'
    thrown(GroovyRuntimeException)
  }

  def 'run a workflow without an environment'() {
    setup:
    def environment = create_environment(name: 'beta')
    def workflow = create_workflow(name: 'deploy candidate', environment: environment)

    when: 'the environment name does not match'
    new WorkflowRunner(create_buildlife(workflow: workflow))
      .request('deploy: candidate', 'dev', [:])

    then: 'an error is raised'
    thrown(GroovyRuntimeException)
  }

  def 'run a workflow by name with two matches that share an environment'() {
    setup:
    def environment = create_environment(name: 'load')
    def workflow1 = create_workflow(name: 'deploy candidate', environment: environment)
    def workflow2 = create_workflow(name: 'deploy staging', environment: environment)

    when:
    new WorkflowRunner(create_buildlife(workflows: [workflow1, workflow2]))
      .request('deploy', 'load', [:])

    then: 'an error is raised'
    thrown(GroovyRuntimeException)
  }

  def create_workflow(map) {
    def name = map['name'] ?: ''
    def active = map['active'] == null ?
      true : map['active']
    def originating = map['originating'] == null ?
      false : map['originating']

    def environments = map['environments'] ?: []
    if (map['environment'])
      environments += map['environment']

    Mock(Workflow) {
      getName() >> name
      isActive() >> active
      isOriginating() >> originating
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
    def workflows = map['workflows'] ?: []
    if (map['workflow'])
      workflows += map['workflow']

    def project = Mock(Project)
    project.getWorkflowArray() >> workflows

    Mock(BuildLife) {
      getProject() >> project
    }
  }
}



