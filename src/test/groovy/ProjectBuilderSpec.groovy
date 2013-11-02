package hilltop

import spock.lang.*
import hilltop.anthill.*
import hilltop.colonies.*
import com.urbancode.anthill3.persistence.*
import com.urbancode.anthill3.domain.project.*
import com.urbancode.anthill3.domain.folder.*
import com.urbancode.anthill3.domain.lifecycle.*

class ProjectBuilderSpec extends Specification {
/* Seems like a dead-end if it can't be mocked...
  def 'build a new project'() {
    setup:
    def folder = new Folder(true)
    folder.name = '/'

    def folders = Mock(FolderFinder)
    folders.one('/') >> folder

    def lifecycle = new LifeCycleModel()
    lifecycle.name = 'lifecycle-1'

    def lifecycles = Mock(LifecycleFinder)
    lifecycles.one('lifecycle-1') >> lifecycle

    def builder = new ProjectBuilder(
      name: 'a',
      folders: folders,
      lifecycles: lifecycles
    )
    builder.description('abc xyz')

    when:
      def project = builder.build()
    then:
      project.name == 'a'
      project.folder.name == '/'
      project.description == 'abc xyz'
      project.lifeCycleModel.name == 'lifecycle-1'
  }
*/
}
