package net.rawyler.struts

import scala.xml._

import org.jgrapht.Graph
import org.jgrapht.DirectedGraph
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge


class StrutsXmlReader {
  
  val ActionPath = """.*?(\w+)""".r
  
  val ForwardPath = """.*?(\w+)\..*""".r
  
  /**
   * load a struts file
   */
  def fromXML(filename: String) : Graph[String, DefaultEdge] = {
    
    val root = xml.XML.loadFile(filename)
    
    val xmlActions = root \ "action-mappings" \ "action"
    
    val actions = for {
      xmlAction <- xmlActions
    } yield makeStrutsAction(xmlAction)
    
    val directedGraph = new DefaultDirectedGraph[String, DefaultEdge](classOf[DefaultEdge])
    
    // add all the vertices (of all struts xml files)
    for(action <- actions){
      directedGraph.addVertex(action.path)
    }
    
    // connect the vertices with edges
    for(action <- actions){
      for(forward <- action.forwards){
        try {
          directedGraph.addEdge(action.path, forward.path)
        } catch {
          case e: IllegalArgumentException => println("Missing: " + action.path + " -> " + forward.path)
        }
      }
    }
    
    directedGraph
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
