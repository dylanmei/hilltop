package hilltop

import spock.lang.*
import hilltop.anthill.*
import hilltop.colonies.*
import com.urbancode.anthill3.domain.project.*

class ProjectBuilderSpec extends Specification {
  def 'build a project'() {
    setup:
    def builder = new ProjectBuilder(name: 'a')
    builder.description('abc xyz')

    when:
      def project = builder.build()
    then:
      project.name == 'a'
      project.folder.name == '/'
      project.description == 'abc xyz'
  }
}
