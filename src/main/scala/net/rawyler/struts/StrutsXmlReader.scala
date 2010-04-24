package net.rawyler.struts

import scala.xml._

class StrutsXmlReader {
  
  val ActionPath = """.*?(\w+)""".r
  
  val ForwardPath = """.*?(\w+)\..*""".r
  
  /**
   * load a struts file
   */
  def fromXML(filenames: String*): Seq[StrutsAction] = {
    
    // directly flatMap to all "action" elements
    val xmlActions = (for (filename <- filenames)
      yield xml.XML.loadFile(filename) \ "action-mappings" \ "action") flatMap (_.toList)
    
    for {
      xmlAction <- xmlActions
    } yield makeStrutsAction(xmlAction)
    
  }
  
  /**
   * Create a new StrutsAction for the given action xml node
   */
  private def makeStrutsAction(xmlAction: Node): StrutsAction = {
    val strutsForwards = for {
      xmlForward <- xmlAction \ "forward"
    } yield makeStrutsForward(xmlForward)
    
    val ActionPath(path) = (xmlAction \ "@path").toString
    
    new StrutsAction(
      path,
      (xmlAction \ "@type").toString,
      (xmlAction \ "@name").toString,
      stringToBoolean((xmlAction \ "@validate").toString),
      (xmlAction \ "@input").toString,
      strutsForwards
    )
  }
  
  /**
   * Create a new StrutsForward for the given xml node
   */
  private def makeStrutsForward(xmlForward: Node): StrutsForward = {
    val ForwardPath(path) = (xmlForward \ "@path").toString
    
    new StrutsForward(
      (xmlForward \ "@name").toString,
      path,
      stringToBoolean((xmlForward \ "@contextRelative").toString)
    )
  }
  
  /**
   * Simple string to boolean that translates an empty string to false
   * could also override .toBoolean in custom predefs
   */
  private def stringToBoolean(s: String) : Boolean = s match {
    case "" => false
    case _ => s.toBoolean
  }
}
