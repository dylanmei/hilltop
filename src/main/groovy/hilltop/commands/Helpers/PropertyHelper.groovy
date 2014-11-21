package hilltop.commands

class PropertyHelper {

  def static toMap(properties) {
    properties.collect {
        def property, value = ''
        def matcher = (it =~ /([^\s=]+)=(.*)/)
        if (!matcher.matches())
          quit "<$it> is invalid, properties should be in format of 'x=y'"
        matcher[0].tail()
    }
  }
}
