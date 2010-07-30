package net.rawyler.struts

import scala.xml._

class StrutsXmlReader extends StrutsReaders {

  /*
   * INFO - INFO - INFO - INFO - INFO - INFO - INFO
   * Link about Load-Failure with DTD:
   * http://stackoverflow.com/questions/1096285/is-scala-java-not-respecting-w3-excess-dtd-traffic-specs
   */

  val ActionPath = """.*?(\w+)""".r
  
  val ForwardPath = """.*?(\w+)\..*""".r
  
  /**
   * Read a XML Struts file, return all vertices (incl. duplicates)
   */
  def fetchVertices(filePath: String) = {
	  val xmlActions = xml.XML.loadFile(filePath) \ "action-mappings" \ "action"
	  // catch all vertices (ignore duplicates)
	  (for { xmlAction <- xmlActions } yield makeVertices(xmlAction)).flatten
  }

 /**
  * Read a XML Struts file, return all edges (incl. duplicates)
  */
  def fetchEdges(filePath: String, vertices: Map[String, VertexRepresentator]) = {
	  val xmlActions = xml.XML.loadFile(filePath) \ "action-mappings" \ "action"
	  // cath all edges (ignore duplicates)
	  (for { xmlAction <- xmlActions } yield makeEdges(xmlAction, vertices)).flatten
  }

  
  /**
   * makeVertices() for given Action Node
   */
  private def makeVertices(xmlAction: Node) = {
	
	val ActionPath(actionPath) = (xmlAction \ "@path").toString

	val vertices = for {
		xmlForward <- xmlAction \ "forward"
		val ForwardPath(forwardPath) = (xmlForward \ "@path").toString
	} yield new VertexRepresentator(forwardPath)
	
	(vertices ++ Seq(new VertexRepresentator(actionPath, true)))
  }
  
  /**
   * makeEdges() for given Action Node with unique vertices
   */
  private def makeEdges(xmlAction: Node, vertices: Map[String, VertexRepresentator]) = {
	  val ActionPath(actionPath) = (xmlAction \ "@path").toString
	  
	  val vertexTail = vertices(actionPath)
	  
	  for {
        xmlForward <- xmlAction \ "forward"
        val edgeName = (xmlForward \ "@name").toString
        val ForwardPath(forwardPath) = (xmlForward \ "@path").toString
	  } yield new EdgeRepresentator(edgeName, vertexTail, vertices(forwardPath)) 
  }
  

}
