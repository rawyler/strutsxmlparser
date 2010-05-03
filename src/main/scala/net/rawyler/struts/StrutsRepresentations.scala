package net.rawyler.struts

class StrutsForward (val name: String, val path: String, val contextRelative: Boolean) {
  require(name != "" && path != "")
}

class StrutsRepresentations (val path: String, val typeString: String, val name: String, val validate: Boolean, val input: String, val forwards: Seq[StrutsForward]) {
  require(name != "" && path != "" && input != "")
}