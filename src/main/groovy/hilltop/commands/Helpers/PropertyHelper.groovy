package hilltop.commands

import static hilltop.Global.quit

class PropertyHelper {

  def static fromArguments(args) {
    args.inject([:], { map, arg ->
        def matcher = (arg =~ /([^\s=]+)=(.*)/)
        if (!matcher.matches())
          quit "<$arg> is invalid, properties should be in format of 'x=y'"

        def property = matcher[0].tail().first()
        def value = matcher[0].tail().last()
        map[property] = value
        map
    })
  }

  def static fromFile(propertiesPath) {
    def props = new Properties()
    def file = new File(propertiesPath)

    if (!file.exists()) {
      quit "Properties file <$propertiesPath> is invalid or it does not exist"
    }

    file.withInputStream {
      props.load(it)
    }
    props
  }
}
