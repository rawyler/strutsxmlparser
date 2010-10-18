package net.rawyler.struts

import scala.swing._

import scala.swing.event._

import java.awt.{Color,Toolkit}

import javax.swing.{JFrame, JScrollPane, UIManager}

import javax.swing.filechooser.FileNameExtensionFilter

import java.io.{ File, IOException }

import edu.uci.ics.jung.visualization.GraphZoomScrollPane

import edu.uci.ics.jung._

/**
 * Read XML files and generate the graph
 */
object App extends SimpleSwingApplication {
   	
  // val reader = new StrutsXmlReader
  val reader = new StrutsReader
  
  // val graphCreator = new GraphCreator
  val jung = new JUNGFacade
  
  var component: GraphZoomScrollPane = null
  
  var verticesG = Map[String, VertexRepresentator]()
  var edgesG = Map[Int, EdgeRepresentator]()
  
  val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
  
  var currentFiles: Seq[File] = null
 
  val filterLabel = new Label {
      text = " Filter nodes: "
    }
  
  val filterField = new TextField()

  val buttonPanel = new BoxPanel(Orientation.Horizontal) {
	
	border = Swing.EmptyBorder(10,10,10,10)
	  
    val openFilesButton = new Button {
      text = "Open file(s)"
    }
	val saveGraphAsButton = new Button {
	  text = "Save graph as ..."
	  visible = false
	}
    val clearFilterButton = new Button {
      text = "Clear filter"
    }	
	
    contents += openFilesButton
    contents += saveGraphAsButton
    contents += filterLabel
    contents += filterField
    contents += clearFilterButton
    listenTo(openFilesButton)
    listenTo(saveGraphAsButton)
    listenTo(filterField)
    listenTo(clearFilterButton)

    reactions += {
      case ButtonClicked(b) =>
      	if(b == openFilesButton) openFile
      	if(b == saveGraphAsButton) saveGraphAsFile
      	if(b == clearFilterButton) clearFilter
      case EditDone(`filterField`) =>
        //val f = filterField.text.toInt
        //val c = f + 1
        //filterField.text = c.toString
        //filterVertices("test")
      	filterVertices(filterField.text.toString)
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
	 	  		"* press [Shift] or [CTRL] to transform graph\n\n" +
	 	  		"To import a previously saved graph name the XML file\n" + 
	 	  		"graph.xml to make sure the right parser is chosen."
	  }
  }
   
  val drawPanel = new BoxPanel(Orientation.Vertical) {
	  
	  background = Color.white
	  
	  def draw(vertices: Map[String, VertexRepresentator], edges: Map[Int, EdgeRepresentator]) = {
	 	  contents += Component.wrap(jung.buildGraph(vertices, edges).prepareLayout.formDesign.activateControllers.plot)
	  }
	  def draw(file: File) = {
	 	  contents += Component.wrap(jung.loadGraphFrom(file).prepareLayout.formDesign.activateControllers.plot)
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
  
  def openFile {
    val fileChooser = getFileChooser
    fileChooser.title = "Choose Struts file(s) to analyse or graph XML file (graph.xml)"
    fileChooser.showOpenDialog(drawPanel) match {
      case FileChooser.Result.Approve =>
      	try {
            if (fileChooser.selectedFile.exists) {
            	
            	currentFiles = fileChooser.selectedFiles
            	
            	if (currentFiles.first.getName == "graph.xml") {
            		drawPanel.draw(currentFiles.first)
            	} else {              
	            	val fileNames = for (currentFile <- currentFiles) yield currentFile.getAbsolutePath
	
	            	// XML & JSP
	            	val (vertices, edges) = reader.readFiles(fileNames:_*)
	              
	            	verticesG = vertices
	            	edgesG = edges
	              
	            	drawPanel.draw(vertices, edges)
	              
	            	buttonPanel.saveGraphAsButton.visible = true
            	}
            }
          } catch {
            case e: IOException =>
              Dialog.showMessage(drawPanel, "Error loading File: " + e.getMessage)

          }
        case FileChooser.Result.Cancel => ;
        case FileChooser.Result.Error => ;
      }
  }

  def saveGraphAsFile = {
	  val dir = new File(System.getProperty("user.home"))
      val fileChooser = new FileChooser(dir) {
        fileFilter = new FileNameExtensionFilter("Image (*.jpg, *.png, *.gif)", "jpg", "png", "gif", "jpeg", "xml")
        title = "Save graph as file"
	  }
	  fileChooser.showSaveDialog(drawPanel) match {
		  case FileChooser.Result.Approve => jung.saveGraphAs(fileChooser.selectedFile)
		  case FileChooser.Result.Cancel => ;
	 	  case FileChooser.Result.Error => ;
	  }
  }
	  
  def filterVertices(filterString: String) = {
	  println("filterVertices "+filterString)
	  println("before: "+edgesG.size)
	  
	  var edges = edgesG.filter(e => e._2.head.name == filterString||e._2.tail.name == filterString)
	  var vertices = verticesG.filterKeys(_ == filterString)

	  println("after: "+edges.size)
	  
	  drawPanel.contents.clear  
	  drawPanel.draw(vertices, edges)
  }
 
  def clearFilter() = {	  
	  drawPanel.contents.clear
	  drawPanel.draw(verticesG, edgesG)
  }
}
