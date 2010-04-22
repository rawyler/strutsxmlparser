package net.rawyler.struts

import scala.swing._
import javax.swing.JScrollPane

import java.awt.Dimension

import org.jgrapht.ext.JGraphModelAdapter

import org.jgraph.JGraph

/**
 * Read XML and generate graph
 */
object App extends SimpleGUIApplication {
  
  def top = new MainFrame {
    title = "Struts Actions and Forwards"
    
    minimumSize = new Dimension(1024, 768)
    
    val reader = new StrutsXmlReader
    
    val graph = reader.fromXML("struts.xml")
    
    val jgraph = new JScrollPane(new JGraph(new JGraphModelAdapter(graph)))
    jgraph.setMinimumSize(new Dimension(1024, 768))
    
    contents = new BoxPanel(Orientation.Vertical) {
      contents += Component.wrap(jgraph)
    }
    
  }  
}
