package net.rawyler.struts

import scala.swing._

import java.awt.Color

import java.awt.image.BufferedImage

import javax.imageio.ImageIO

import java.io.File

import edu.uci.ics.jung.graph.DirectedGraph

import edu.uci.ics.jung.graph.DirectedSparseGraph

import edu.uci.ics.jung.visualization.VisualizationViewer

import edu.uci.ics.jung.visualization.BasicVisualizationServer

import edu.uci.ics.jung.algorithms.layout.Layout

import edu.uci.ics.jung.algorithms.layout.FRLayout

import edu.uci.ics.jung.algorithms.layout.KKLayout

import edu.uci.ics.jung.visualization.GraphZoomScrollPane

import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse

import edu.uci.ics.jung.visualization.control.ModalGraphMouse

import edu.uci.ics.jung.visualization.decorators.ToStringLabeller

import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer

import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer

import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position

import edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer

import edu.uci.ics.jung.algorithms.layout.util.RandomLocationTransformer

class JUNGFacade {
	
	var graph: DirectedGraph[VertexRepresentator, EdgeRepresentator] = null
	
	var layout: Layout[VertexRepresentator, EdgeRepresentator] = null
	
	var vv: VisualizationViewer[VertexRepresentator, EdgeRepresentator] = null
	
	def buildGraph(vertices: Map[String, VertexRepresentator], edges: Map[Int, EdgeRepresentator]) = {
		
		graph = new DirectedSparseGraph[VertexRepresentator, EdgeRepresentator]
		
		// add all the vertices to the graph
		vertices foreach { case (key, v) => if(v.alive) graph.addVertex(v) }
		
		// connect the vertices with edges
		edges foreach { case (key, edge) =>
			if(edge.tail.alive && edge.head.alive) graph.addEdge(edge, edge.tail, edge.head)
			else println("Missing: " + edge + " [" + edge.tail + " -> " + edge.head + "]")
		}
		// OTHER: prints out every edge (incl. dead ones): edges foreach { case (key, edge) => directedGraph.addEdge(edge, edge.tail, edge.head) }
		
		this
	}
	
	def prepareLayout = {
		// val layout = new FRLayout[VertexRepresentator, EdgeRepresentator](graph)
		layout = new KKLayout[VertexRepresentator, EdgeRepresentator](graph)
	
		this
	}
	
	def formDesign = {
		vv =  new VisualizationViewer[VertexRepresentator, EdgeRepresentator](layout)
		
		vv.setBackground(Color.white)
		
		// design of vertices
		vv.getRenderer().setVertexRenderer(
				new GradientVertexRenderer[VertexRepresentator, EdgeRepresentator](
        				Color.white, Color.red, 
        				Color.white, Color.blue,
        				vv.getPickedVertexState(),
        				false))
        				
        // design of vertex 'labels
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller[VertexRepresentator])
        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR)

        // design of edge 'labels
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller[EdgeRepresentator])
        		
        // add a listener for ToolTips
        vv.setVertexToolTipTransformer(new ToStringLabeller[VertexRepresentator])
        
        this
	}

	def activateControllers = {       
        // MOUSE: press 'p' for picking points, press 't' for transforming tree
        val graphMouse = new DefaultModalGraphMouse[VertexRepresentator, EdgeRepresentator]
        vv.setGraphMouse(graphMouse)

        vv.addKeyListener(graphMouse.getModeKeyListener())
        
        this
	}
	
	def plot = new GraphZoomScrollPane(vv)
	
	def saveGraphAs(file: File): Unit = {
	  /**
	   * Convert the viewed graph to a JPEG image written in the specified file name.
	   * The size of the image is the size of the layout. The background color is this one
	   * of the visualization viewer. The default representation of the image is 8-bit RGB
	   * color components, corresponding to a Windows- or Solaris- style BGR color model,
	   * with the colors Blue, Green, and Red packed into integer pixels.
	   */
		
		val FileType = """.*?\.(.+)$""".r
		val FileType(fileType) = file.getName
		val width = vv.getWidth
		val height = vv.getHeight
		val bg = Color.white

		val bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR)
		val graphics = bi.createGraphics()
	
		vv.paint(graphics)

		try{
			ImageIO.write(bi, fileType, file)
		} catch {
			case e: Exception => e.printStackTrace()
		}

		
  }
}