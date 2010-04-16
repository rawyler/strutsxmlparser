package net.rawyler.struts

class StrutsAction (val path: String, val typeString: String, val name: String, val validate: Boolean, val input: String, val forwards: Seq[StrutsForward]) {
  require(name != "" && path != "" && input != "")
}