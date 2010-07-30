package net.rawyler.struts

import scala.collection.mutable

class StrutsReader {
	
	val FileType = """.*?\.(.+)$""".r

	val strutsReaders = Map[String, StrutsReaders](
			"jsp" -> new StrutsJspReader,
			"jspf" -> new StrutsJspReader,
			"xml" -> new StrutsXmlReader
			)
	
	def readFiles(fileNames: String*) = {

		// read all files and get all vertices and edges
		val verticesCache = (for {
			fileName <- fileNames
		} yield fetchVertices(fileName)).flatten  
	
		// elaminate duplicates and let vertices alive
		val verticesMut = mutable.Map.empty[String, VertexRepresentator]
		for {
			v <- verticesCache
			if(! verticesMut.contains(v.name) || ! verticesMut(v.name).alive)
		} yield verticesMut += (v.name -> v)
		val vertices = Map[String, VertexRepresentator]() ++ verticesMut
		
		// cath all edges (ignore duplicates)
		val edgesCache = (for {
			fileName <- fileNames
		} yield fetchEdges(fileName, vertices)).flatten
	
		// create Map (rename duplicates)
		val edgesMut = mutable.Map.empty[Int, EdgeRepresentator]
		for {
			e <- edgesCache
		} yield edgesMut += (e.hashCode -> e) 
		val edges = Map[Int, EdgeRepresentator]() ++ edgesMut
		
		(vertices, edges)
	}
	
	def fetchVertices(filePath:String) = {
		val FileType(fileType) = filePath
		(strutsReaders(fileType)).fetchVertices(filePath)
	}
	
	def fetchEdges(filePath:String, vertices: Map[String, VertexRepresentator]) = {
		val FileType(fileType) = filePath
		(strutsReaders(fileType)).fetchEdges(filePath, vertices)
	}

}

trait StrutsReaders {
	def fetchVertices(fileName: String): Seq[VertexRepresentator]
	def fetchEdges(fileName: String, vertices: Map[String, VertexRepresentator]): Seq[EdgeRepresentator]
}