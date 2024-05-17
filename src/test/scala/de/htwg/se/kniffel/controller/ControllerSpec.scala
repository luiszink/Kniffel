package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.util.Observer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ControllerSpec extends AnyWordSpec with Matchers {

  "A Controller" should {

    "initialize with default values" in {
      val controller = new Controller
      controller.repetitions should be (2)
      controller.getDice.length should be (5)
    }

    "add a player" in {
      val controller = new Controller
      controller.addPlayer("Player1")
      controller.getCurrentPlayer.name should be ("Player1")
    }

    "roll dice and keep some" in {
      val controller = new Controller
      controller.addPlayer("Player1")
      val initialDice = controller.getDice
      controller.keepDice(List(1, 2, 3))
      controller.repetitions should be (1)
      val keptDice = controller.getDice
      initialDice.slice(0, 3) should contain theSameElementsAs keptDice.slice(0, 3)
    }

    "switch to the next player" in {
      val controller = new Controller
      controller.addPlayer("Player1")
      controller.addPlayer("Player2")
      controller.nextPlayer()
      controller.getCurrentPlayer.name should be ("Player2")
    }

    "reset repetitions after switching players" in {
      val controller = new Controller
      controller.addPlayer("Player1")
      controller.addPlayer("Player2")
      controller.keepDice(List(1, 2, 3))
      controller.keepDice(List(1, 2, 3))
      controller.nextPlayer()
      controller.repetitions should be (2)
    }

    "handle illegal score updates gracefully" in {
      val controller = new Controller
      controller.addPlayer("Player1")
      controller.setScoreUpdater("y") // Set a score updater
      intercept[IllegalArgumentException] {
        controller.updateScore("invalidCategory")
      }
    }
  }
}
