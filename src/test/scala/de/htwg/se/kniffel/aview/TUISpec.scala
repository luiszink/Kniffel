package de.htwg.se.kniffel.aview

import de.htwg.se.kniffel.controller.Controller
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.io.StdIn

class TUITest extends AnyWordSpec with Matchers {
  "A TUI" when {
    "new" should {
      "display the initial game state" in {
        val controller = new Controller()
        val tui = new TUI(controller)
        tui.printGame() should include ("+---+---+---+---+---+")
      }
    }

    "processing input" should {
      "end the game if 'f' is input" in {
        val controller = new Controller()
        val tui = new TUI(controller)
        // Simulate user input
        Console.withIn(new java.io.StringReader("f\n")) {
          tui.input() shouldBe None
        }
      }
    }
  }
}
