package hilltop.colonies

import com.urbancode.anthill3.domain.project.*

class Colony {
  Project project
  ArrayList workflows
}
/*
class Project {
  String name
  String folder
  String description
  String lifecycle
  String environment
  String sourceType??
}
*/

class Workflow {
  String name
  Boolean originating
}
