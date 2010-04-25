package net.rawyler.struts

class Point (val x: Int, val y: Int) {
  
  override def equals(other: Any) = other match {
    case that: Point => (that canEqual this) &&
      this.x == that.x && this.y == that.y
    
    case _ => false
  }
  
  override def hashCode: Int =
    41 * (
      41 + x.hashCode
    ) + y.hashCode
  
  def canEqual(other: Any) = other.isInstanceOf[Point]
  
  override def toString: String = "(" + x + ", " + y +")"
  
  def distance (p1: Point, p2: Point): Int = {
    Math.sqrt((p1.x - p2.x)^2 + (p1.y - p2.y)^2)
  }
}
