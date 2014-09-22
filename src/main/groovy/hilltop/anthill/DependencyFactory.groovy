
package hilltop.anthill

import com.urbancode.codestation2.domain.project.*

import com.urbancode.anthill3.domain.profile.*

@Mixin(FeedbackHelper)
class DependencyFactory {
  def DependencyFactory(Closure handlers) {
    if (handlers) init_feedback(handlers)
  }

  def create(dependentWorkflow, dependencyWorkflow, artifactSet, location) {

    def status = new StatusFinder().one(dependentWorkflow, "success")
    if (status == null)
       error "Cannot find success status for <$dependentWorkflow.name>"
    
    def dependency = new Dependency(
      new AnthillProject(dependentWorkflow.buildProfile), 
      new AnthillProject(dependencyWorkflow.buildProfile))

    dependency.setBuildConditionToDefault()
    dependency.status = status
    dependency.addSet2Dir(artifactSet, location, /* transitive */ false)

    dependency
  }
}