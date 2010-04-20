package net.rawyler.struts

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4}

class StrutsActionTest extends JUnit4(StrutsActionTestSpec)

object StrutsActionTestSpecRunner extends ConsoleRunner(StrutsActionTestSpec)

object StrutsActionTestSpec extends Specification {
  "A StrutsAction" should {
    "have a name and path equal to the passed values" in {
      val strutsAction = new StrutsAction("thePath", "theType", "theName", true, "theInput", Nil)
      
      strutsAction.name mustMatch "theName"
      strutsAction.path mustMatch "thePath"
      strutsAction.typeString mustMatch "theType"
      strutsAction.input mustMatch "theInput"
      strutsAction.forwards.size must be(0)
    }

  }
}
