
package hilltop.anthill

import com.urbancode.anthill3.domain.artifacts.*

@Mixin(FeedbackHelper)
class ArtifactFinder {
  def ArtifactFinder(Closure handlers) {
    if (handlers) init_feedback(handlers)
  }

  def one(name) {
      def artifactSetGroupFactory = ArtifactSetGroupFactory.getInstance()
      def artifactSetGroups = artifactSetGroupFactory.restoreAll()
      for (ArtifactSetGroup group : artifactSetGroups) {
        def test = group.getArtifactSet(name) 
        if (test != null) {
          return test
        }
     }
  }
}