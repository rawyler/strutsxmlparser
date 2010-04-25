package net.rawyler.struts

import scala.swing._

import scala.swing.event.ButtonClicked

import javax.swing.{JFrame, JScrollPane, UIManager}

import java.awt.Dimension

import javax.swing.filechooser.FileNameExtensionFilter

import java.io.{ File, IOException }

import org.jgrapht.ext.JGraphModelAdapter

import org.jgrapht.graph.DefaultEdge

import org.jgraph.JGraph


/**
 * Read XML files and generate the graph
 */
object App extends SimpleGUIApplication {
  
  final val WIDTH = 1600
  
  final val HEIGHT = 1024
  
  val reader = new StrutsXmlReader
  
  val graphCreator = new GraphCreator
  
  var currentFiles: Seq[File] = null
  
  val mainPanel = new BoxPanel(Orientation.Vertical) {
    val openFilesButton = new Button {
      text = "Open file(s)"
    }
    contents += openFilesButton

    listenTo(openFilesButton)
    
    def getFileChooser = {
      val dir = if (currentFiles == null) new File(System.getProperty("user.home")) else currentFiles.first
      new FileChooser(dir) {
        fileFilter = new FileNameExtensionFilter("Struts XML", "xml")
        // strange behaviour of multiSelectionEnable = true, had to use _
        multiSelectionEnable_= (true)
      }
    }
    
    def openFile: Unit = {
      val fileChoser = getFileChooser
      fileChoser.title = "Choose Struts XML file(s) to analyse"
      fileChoser.showOpenDialog(this) match {
        case FileChooser.Result.Approve =>
          try {
            if (fileChoser.selectedFile.exists) {
              currentFiles = fileChoser.selectedFiles
              val fileNames = for (currentFile <- currentFiles) yield currentFile.getAbsolutePath
              
              val fromXML = reader.fromXML(fileNames:_*)
              val graph = graphCreator.toGraph(fromXML)
              val adapter = new JGraphModelAdapter(graph);
              val jScrollPane = new JScrollPane(new JGraph(adapter))
              jScrollPane.setMinimumSize(new Dimension(WIDTH, HEIGHT))
              
              val vertexPositioner = new VertexPositioner[String, DefaultEdge]
              vertexPositioner.positionVertices(adapter, fromXML, (WIDTH, HEIGHT))
              
              contents += Component.wrap(jScrollPane)
              
              jScrollPane.updateUI
            }
          } catch {
            case e: IOException =>
              Dialog.showMessage(this, "Error loading File: " + e.getMessage)
          }
        case FileChooser.Result.Cancel => ;
        case FileChooser.Result.Error => ;
      }
    }
    
    reactions += {
      case ButtonClicked(openFilesButton) => openFile
    }
  }
  
  def top = new MainFrame {
    title = "Struts Actions and Forwards"
    
    UIManager setLookAndFeel UIManager.getSystemLookAndFeelClassName
    
    JFrame setDefaultLookAndFeelDecorated true
    
    minimumSize = new Dimension(WIDTH, HEIGHT)
    
    contents = mainPanel
  }  
}
