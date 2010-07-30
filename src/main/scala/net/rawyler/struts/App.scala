package net.rawyler.struts

import scala.swing._

import scala.swing.event.ButtonClicked

import java.awt.{Color,Toolkit}

import javax.swing.{JFrame, JScrollPane, UIManager}

import javax.swing.filechooser.FileNameExtensionFilter

import java.io.{ File, IOException }


/**
 * Read XML files and generate the graph
 */
object App extends SimpleSwingApplication {
   	
  // val reader = new StrutsXmlReader
  val reader = new StrutsReader
  
  // val graphCreator = new GraphCreator
  val jung = new JUNGFacade
      
  val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
  
  var currentFiles: Seq[File] = null

  val buttonPanel = new BoxPanel(Orientation.Horizontal) {
	
	border = Swing.EmptyBorder(10,10,10,10)
	  
    val openFilesButton = new Button {
      text = "Open file(s)"
    }
	val saveGraphAsButton = new Button {
	  text = "Save graph as ..."
	  visible = false
	}
	
    contents += openFilesButton
    contents += saveGraphAsButton

    listenTo(openFilesButton)
    listenTo(saveGraphAsButton)

    reactions += {
      case ButtonClicked(b) =>
      	if(b == openFilesButton) openFile
      	if(b == saveGraphAsButton) saveGraphAsFile
    }
  }
  
  val infoPanel = new BoxPanel(Orientation.Vertical) {
	  	  
	  contents += new Label("Interactive modes:")
	  contents += new TextArea {
	 	  background = Color.lightGray
	 	  text = "[p] - picking mode\n" +
	 	  		"* drag and drop vertices\n" +
	 	  		"* press [Shift] to select multiple vertices\n" +
	 	  		"* press [CTRL] to center a specific vertex\n\n" +
	 	  		"[t] - transforming mode\n" +
	 	  		"* move graph around\n" +
	 	  		"* scroll to zoom in and out\n" +
	 	  		"* press [Shift] or [CTRL] to transform graph"
	  }
  }
   
  val drawPanel = new BoxPanel(Orientation.Vertical) {
	  
	  background = Color.white

	  def draw(vertices: Map[String, VertexRepresentator], edges: Map[Int, EdgeRepresentator]) = {
	 	contents += Component.wrap(jung.buildGraph(vertices, edges).prepareLayout.formDesign.activateControllers.plot)
	  }
	  
  }
  
  def top = new MainFrame {
    title = "Struts Actions and Forwards"
    
    UIManager setLookAndFeel UIManager.getSystemLookAndFeelClassName
    
    JFrame setDefaultLookAndFeelDecorated true
    
    minimumSize = screenSize
    
    contents = new BorderPanel {
    	layout += buttonPanel -> BorderPanel.Position.North
    	layout += drawPanel -> BorderPanel.Position.Center
    	layout += infoPanel -> BorderPanel.Position.East
    }
  }
  
  def getFileChooser = {
      val dir = if (currentFiles == null) new File(System.getProperty("user.home")) else currentFiles.first
      new FileChooser(dir) {
        fileFilter = new FileNameExtensionFilter("Struts Files", "xml", "jsp", "jspf")
        
        multiSelectionEnabled = true
      }
  }
  
  def openFile: Unit = {
    val fileChooser = getFileChooser
    fileChooser.title = "Choose Struts file(s) to analyse"
    fileChooser.showOpenDialog(drawPanel) match {
      case FileChooser.Result.Approve =>
      	try {
            if (fileChooser.selectedFile.exists) {

              currentFiles = fileChooser.selectedFiles
              
              val fileNames = for (currentFile <- currentFiles) yield currentFile.getAbsolutePath

              // XML & JSP
              val (vertices, edges) = reader.readFiles(fileNames:_*)
  
              drawPanel.draw(vertices, edges)
              
              buttonPanel.saveGraphAsButton.visible = true
            }
          } catch {
            case e: IOException =>
              Dialog.showMessage(drawPanel, "Error loading File: " + e.getMessage)

          }
        case FileChooser.Result.Cancel => ;
        case FileChooser.Result.Error => ;
      }
  }

  def saveGraphAsFile: Unit = {
	  val dir = new File(System.getProperty("user.home"))
      val fileChooser = new FileChooser(dir) {
        fileFilter = new FileNameExtensionFilter("Image (*.jpg, *.png, *.gif)", "jpg", "png", "gif", "jpeg")
        title = "Save graph as file"
	  }
	  fileChooser.showSaveDialog(drawPanel) match {
		  case FileChooser.Result.Approve => jung.saveGraphAs(fileChooser.selectedFile)
		  case FileChooser.Result.Cancel => ;
	 	  case FileChooser.Result.Error => ;
	  }
  }
}
