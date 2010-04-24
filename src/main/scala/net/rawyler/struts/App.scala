package net.rawyler.struts

import scala.swing._

import javax.swing.JScrollPane

import java.awt.Dimension

import org.jgrapht.ext.JGraphModelAdapter

import org.jgraph.JGraph

/**
 * Read XML files and generate the graph
 */
object App extends SimpleGUIApplication {
  
  val reader = new StrutsXmlReader
  
  val graphCreator = new GraphCreator
  
  def top = new MainFrame {
    title = "Struts Actions and Forwards"
    
    minimumSize = new Dimension(1024, 768)
    
    val graph = graphCreator.toGraph(reader.fromXML("struts.xml"))
    
    val adapter = new JGraphModelAdapter(graph);
    
    val jScrollPane = new JScrollPane(new JGraph(adapter))
    jScrollPane.setMinimumSize(new Dimension(1024, 768))
    
    contents = new BoxPanel(Orientation.Vertical) {
      contents += Component.wrap(jScrollPane)
    }
    
  }  
}
