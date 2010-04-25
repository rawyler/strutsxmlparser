package net.rawyler.struts

import scala.swing._

import scala.swing.event.ButtonClicked

import javax.swing.{JFrame, JScrollPane, UIManager}

import java.awt.Dimension

import javax.swing.filechooser.FileNameExtensionFilter

import java.io.{ File, IOException }

import org.jgrapht.ext.JGraphModelAdapter

import org.jgraph.JGraph

/**
 * Read XML files and generate the graph
 */
object App extends SimpleGUIApplication {
  
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
              
              val graph = graphCreator.toGraph(reader.fromXML(fileNames:_*))
              val adapter = new JGraphModelAdapter(graph);
              val jScrollPane = new JScrollPane(new JGraph(adapter))
              jScrollPane.setMinimumSize(new Dimension(1024, 768))
              
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
    
    minimumSize = new Dimension(1024, 768)
    
    contents = mainPanel
  }  
}
