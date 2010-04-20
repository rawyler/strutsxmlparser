package net.rawyler.struts

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4}

class StrutsForwardTest extends JUnit4(StrutsForwardTestSpec)

object StrutsForwardTestSpecRunner extends ConsoleRunner(StrutsForwardTestSpec)

object StrutsForwardTestSpec extends Specification {
  "A StrutsForward" should {
    "have a name and path equal to the passed values" in {
      val strutsForward = new StrutsForward("theName", "thePath", true)
      
      strutsForward.name mustMatch "theName"
      strutsForward.path mustMatch "thePath"
    }
    
  }
}

