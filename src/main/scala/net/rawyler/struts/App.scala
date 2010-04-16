package net.rawyler.struts

/**
 * Read XML
 */
object App extends Application {
  val reader = new StrutsXmlReader
  
  reader.fromXML("struts.xml")
}
