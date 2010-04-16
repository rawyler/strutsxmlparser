package net.rawyler.struts

class StrutsForward (val name: String, val path: String, val contextRelative: Boolean) {
  require(name != "" && path != "")
}
