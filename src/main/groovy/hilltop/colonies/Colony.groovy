package hilltop.colonies

class Colony {
  Project project
  ArrayList workflows
}

class Project {
  String name
  String folder
}

class Workflow {
  String name
  Boolean originating
}
