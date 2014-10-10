
package hilltop.commands

import hilltop.anthill.*

class BuildLinkCommands extends AnthillCommands {
  def BuildLinkCommands(out) {
    super(out)
  }

  def list(id) {
    send work {
      def buildlife = finder(BuildFinder).one(id as long)
      buildlife.links.collect {[
        name: it.name,
        description: it.description,
        url: it.url
      ]}
    }
  }

  def add(id, linkName, linkUrl) {
    work {
      def buildlife = finder(BuildFinder).one(id as long)
      buildlife.addLink(linkName, linkUrl)
      buildlife.store()
      statusln("Added link <$linkName> to <$linkUrl>")
    }
  }

  def open(id, linkName) {
    work {
      def buildlife = finder(BuildFinder).one(id as long)
      buildlife.links.each {
        if (it.name == linkName)
          browse(it.url) 
      }
    }
  }
}
