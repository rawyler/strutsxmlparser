package net.rawyler.struts

import scala.xml._

class StrutsXmlReader {
  
  /**
   * load a struts file
   */
  def fromXML(filename: String) : Unit = {
    
    val root = xml.XML.loadFile(filename)
    
    val xmlActions = root \ "action-mappings" \ "action"
    
    val actions = for {
      xmlAction <- xmlActions
    } yield makeStrutsAction(xmlAction)
    
    for(action <- actions){
      println(action.name)
    }
    
    // TODO: fill actions into graph structure
  }
  
  /**
   * Create a new StrutsAction for the given action xml node
   */
  private def makeStrutsAction(xmlAction: Node): StrutsAction = {
    val strutsForwards = for {
      xmlForward <- xmlAction \ "forward"
    } yield makeStrutsForward(xmlForward)
    
    new StrutsAction(
      (xmlAction \ "@path").toString,
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
    new StrutsForward(
      (xmlForward \ "@name").toString,
      (xmlForward \ "@path").toString,
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
