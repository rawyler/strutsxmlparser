package net.rawyler.struts

import org.jgrapht.Graph
import org.jgrapht.DirectedGraph
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

class GraphCreator {
  def toGraph(actions: Seq[StrutsAction]) : Graph[String, DefaultEdge] = {
    val directedGraph = new DefaultDirectedGraph[String, DefaultEdge](classOf[DefaultEdge])
    
    // add all the vertices (of all struts xml files)
    for (action <- actions){
      directedGraph.addVertex(action.path)
    }
    
    // connect the vertices with edges
    for (action <- actions){
      for (forward <- action.forwards){
        try {
          directedGraph.addEdge(action.path, forward.path)
        } catch {
          case e: IllegalArgumentException => println("Missing: " + action.path + " -> " + forward.path)
        }
      }
    }
    
    directedGraph
  }
}
