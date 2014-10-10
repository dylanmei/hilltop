
package hilltop.commands

import hilltop.anthill.*
import com.urbancode.anthill3.domain.workflow.*
import com.urbancode.anthill3.services.build.*
import com.urbancode.anthill3.domain.buildrequest.*
import com.urbancode.anthill3.domain.buildlife.*

class BuildLinkCommands extends AnthillCommands {
  def BuildLinkCommands(out) {
    super(out)
  }

  def list(id) {
    send work {
      def buildlife = finder(BuildFinder).one(id as long)

    quit "Not implemented"
    }
  }

  def add(id, linkName, linkUrl) {
    def request = work {
      def buildlife = finder(BuildFinder).one(id as long)
      buildlife.addLink(linkName, linkUrl)
      buildlife.store()
      statusln("Added link <$linkName> to <$linkUrl>")
    }
  }

  def open(id, linkName) {
    def buildlife = work {
      finder(BuildFinder).one(id as long)
    }
    quit "Not implemented"
  }
}
