package net.rawyler.struts

import org.jgraph.graph.{DefaultGraphCell, GraphConstants, AttributeMap}

import org.jgrapht.ext.JGraphModelAdapter

import java.awt.Rectangle

import scala.collection.mutable.Set

/**
 * This VertexPositioner arranges all the vertices in a circle
 */
class VertexPositioner[V,E] {

  def positionVertices(adapter: JGraphModelAdapter[V,E], strutsActions: Seq[StrutsAction], max: Tuple2[Int, Int]): Unit = {
    var index = 0
    for (a <- strutsActions) {
      index += 1
      positionVertexAt(adapter, a.path, generatePoint(new Point(max._1 / 2, max._2 / 2), index, strutsActions.size))
    }
  }
  
  private def positionVertexAt(adapter: JGraphModelAdapter[V,E], vertex: Any , coordinates: Point ): Unit = {
      val cell = adapter.getVertexCell(vertex)
      val attr = cell.getAttributes
      val rectangle    = GraphConstants.getBounds(attr)

      GraphConstants.setBounds(attr, new Rectangle(coordinates.x, coordinates.y, rectangle.getWidth.toInt, rectangle.getHeight.toInt))
      
      cell.setAttributes(attr)
  }
  
  private def generatePoint(center: Point, index: Int, total: Int): Point = {
    val x = center.x + (center.x * 0.8) * Math.cos((Math.Pi * 2 / total) * index)
    val y = center.y + (center.y * 0.8) * Math.sin((Math.Pi * 2 / total) * index)
    
    new Point(x.toInt, y.toInt)
  }
 
}
