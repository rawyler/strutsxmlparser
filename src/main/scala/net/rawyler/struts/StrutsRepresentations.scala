package net.rawyler.struts

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

/*
 * Vertex
 * name: XML = action@path [alive] && forward@path [alive or dead], JSP = file name [alive] && html:link@path [alive or dead]
 */
class VertexRepresentator (val name: String, val alive: Boolean = false) {
  require(name != "")
  
  override def toString = name
}
