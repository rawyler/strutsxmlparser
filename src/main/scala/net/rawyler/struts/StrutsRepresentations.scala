package net.rawyler.struts

import org.apache.commons.collections15.Transformer

/*
 * Edge
 * name: XML = forward@name, JSP = html:link@page (incl. ?variable=value)
 * 
 */
class EdgeRepresentator (val name: String, val tail: VertexRepresentator, val head: VertexRepresentator) {
  require(name != "" && tail != null && head != null)

  override def hashCode: Int =
	  41 * (
			  41 * (
					  41 + name.hashCode
			  ) + tail.hashCode
	  ) + head.hashCode
  
  override def toString = name
}

object EdgeRepresentator {
  def getTransformerEdgeToString: Transformer[EdgeRepresentator, String] = {
	  return new EdgeRepresentatorIDs()
  }
}

class EdgeRepresentatorIDs extends Transformer[EdgeRepresentator, String] {
	def transform(e: EdgeRepresentator): String = {
		var s: String = null
		s = new String( e.name + " #" + e.hashCode )
		return s
	}
}

/*
 * Vertex
 * name: XML = action@path [alive] && forward@path [alive or dead], JSP = file name [alive] && html:link@path [alive or dead]
 */
class VertexRepresentator (val name: String, val alive: Boolean = false) {
  require(name != "")
  
  override def toString = name
}

