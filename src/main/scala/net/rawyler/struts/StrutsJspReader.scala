package net.rawyler.struts

import scala.collection.mutable

import scala.io.Source

class StrutsJspReader extends StrutsReaders {

  // HtmlLink = <html:link page="/juaus141.do"  transaction="true"><bean:message key="LabelKontoAntraege"/></html:link>
  // val HtmlLink = """(<html:link.*?page="/[a-zA-Z0-9]*.do".*?>)""".r
  val HtmlLink = """(<html:link.*?page=".*?/[^/\"]*\.[^/\"]{2,4}".*html:link>)""".r
 
  // DirectiveInclude = <%@ include file="/jsp/juaus/juaus120/organisation.jsp" %>
  // val DirectiveInclude = """(<%@.*?file=".*?/[^/\"]*\.[^/\"]{3,4}".*%>)""".r
  
  // JSPInclude =  <jsp:include page="/jsp/juaus/juaus130/nutzergruppe1.jsp"/>
  val JSPInclude = """(<jsp:include.*?page=".*?/[^/\"]*\.[^/\"]{3,4}".*/>)""".r

  // HtmlForm = <html:form action="<%= _Controller %>" styleId="form" method="POST">

  // FileNames = xyz.jsp, xyz.jspf, xyz.do
  val FileName = """.*?([^/\"]*)\.[^/\"]{2,4}.*?""".r

  /**
   * Read an JSP Struts file, return all vertices (incl. duplicates)
   */
  def fetchVertices(filePath: String) = {
	  
	  val fileContent = Source.fromFile(filePath, "utf-8").mkString

	  val FileName(fileName) = filePath
	  
	  // catch all vertices (ignore duplicates)
	  val urls = (HtmlLink findAllIn fileContent).toSeq ++ (JSPInclude findAllIn fileContent).toSeq 
	  Seq(new VertexRepresentator(fileName, true)) ++ (for { 
	 	  url <- urls
	 	  val FileName(fileNameURL) = url
	  } yield new VertexRepresentator(fileNameURL))
  }

  /**
  * Read a JSP Struts file, return all edges (incl. duplicates)
  */ 
  def fetchEdges(filePath: String, vertices: Map[String, VertexRepresentator]) = {
	  
	  val fileContent = Source.fromFile(filePath, "utf-8").mkString
	  
	  val FileName(fileName) = filePath
	  
	  // catch all edges (ignore duplicates)
	  val urls = (HtmlLink findAllIn fileContent).toSeq ++ (JSPInclude findAllIn fileContent).toSeq
	  (for { 
	 	  url <- urls
	 	  val FileName(fileNameURL) = url
	  } yield new EdgeRepresentator(url, vertices(fileName), vertices(fileNameURL)))  
  }
  
}