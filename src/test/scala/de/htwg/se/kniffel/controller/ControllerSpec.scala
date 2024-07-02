package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.util._
import de.htwg.se.kniffel.controller.controllerImpl._
import de.htwg.se.kniffel.model.fileIoComponents.fileIoJsonImpl.FileIoJsonImpl
import de.htwg.se.kniffel.model.fileIoComponents.fileIoXmlImpl.FileIoXmlImpl
import com.google.inject.Provider
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar

class DummyState extends StateInterface {
  var handleInputCalled = false
  override def name: String = "DummyState"
  override def handleInput(input: String, controller: ControllerInterface): Unit = {
    handleInputCalled = true
  }
}

class ControllerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A Controller" should {
    val jsonProvider = mock[Provider[FileIoJsonImpl]]
    val xmlProvider = mock[Provider[FileIoXmlImpl]]
    val controller = new Controller(jsonProvider, xmlProvider)

    "add players correctly" in {
      controller.addPlayer("Player1")
      controller.addPlayer("Player2")
      controller.getCurrentPlayer.name shouldEqual "Player1"
      controller.nextPlayer()
      controller.getCurrentPlayer.name shouldEqual "Player2"
    }

    "return the correct current player" in {
      val newController = new Controller(jsonProvider, xmlProvider)
      newController.addPlayer("Player1")
      newController.addPlayer("Player2")
      newController.getCurrentPlayer.name shouldEqual "Player1"
    }

    "switch to the next player correctly" in {
      val newController = new Controller(jsonProvider, xmlProvider)
      newController.addPlayer("Player1")
      newController.addPlayer("Player2")
      newController.nextPlayer()
      newController.getCurrentPlayer.name shouldEqual "Player2"
    }

    "keep dice correctly" in {
      val initialDice = controller.getDice
      controller.keepDice(List(0, 1, 2))
      val newDice = controller.getDice
      newDice should not equal initialDice
      newDice.slice(0, 2) shouldEqual initialDice.slice(0, 2)
    }

    "get previous dice correctly" in {
      val diceValues = controller.getDice
      controller.nextPlayer()
      controller.getPreviousDice shouldEqual diceValues
    }

    "notify observers and update state when repetitions reach 0" in {
      var notified = false
      val observer = new Observer {
        override def update(event: KniffelEvent.Value): Unit = {
          if (event == KniffelEvent.PrintDice) notified = true
        }
      }
      controller.add(observer)
      controller.keepDice(List(0, 1, 2))
      controller.keepDice(List(0, 1, 2)) // This should trigger the notification and state change
      notified shouldEqual true
      controller.getCurrentState shouldBe a[UpdateState]
      controller.repetitions shouldEqual 2
    }

    "reset repetitions after setting state to UpdateState" in {
      val newController = new Controller(jsonProvider, xmlProvider)
      newController.addPlayer("Player1")
      newController.keepDice(List(0, 1, 2))
      newController.keepDice(List(0, 1, 2))
      newController.repetitions shouldEqual 2
    }

    "handle input correctly for 'undo' with previousDice" in {
      val newController = new Controller(jsonProvider, xmlProvider)
      newController.addPlayer("Player1")
      newController.keepDice(List(0, 1, 2)) // Behalte die ersten drei Würfel
      val previousDice = newController.getDice
      newController.updateScore("one")
      newController.handleInput("undo")
      val score = newController.getCurrentPlayer.scoreCard.categories("one")
      score shouldEqual None
      newController.getDice shouldEqual previousDice
    }

    "handle state transitions correctly" in {
      controller.setState(new RollingState())
      controller.getCurrentState shouldBe a[RollingState]
      controller.setState(new UpdateState())
      controller.getCurrentState shouldBe a[UpdateState]
    }

    "handle score updater correctly" in {
      controller.setScoreUpdater("standard")
      // Hier könnte man eine Methode hinzufügen, die den Typ des ScoreUpdaters zurückgibt
      // und diesen dann prüfen.
      // controller.getScoreUpdaterType shouldBe "StandardScoreUpdater"
    }

    "set dice to previousDice on undo" in {
      val newController = new Controller(jsonProvider, xmlProvider)
      newController.addPlayer("Player1")
      newController.keepDice(List(0, 1, 2)) // Behalte die ersten drei Würfel
      val previousDice = newController.getDice
      newController.nextPlayer() // Wechseln Sie den Spieler, um previousDice zu setzen
      newController.handleInput("undo")
      newController.getDice shouldEqual previousDice
    }

    "correctly handle player index on undo" in {
      val newController = new Controller(jsonProvider, xmlProvider)
      newController.addPlayer("Player1")
      newController.addPlayer("Player2")
      newController.nextPlayer() // Aktueller Spieler ist "Player2"
      newController.handleInput("undo") // Sollte zu "Player1" zurückkehren
      newController.getCurrentPlayer.name shouldEqual "Player1"
      newController.nextPlayer() // Aktueller Spieler ist wieder "Player2"
      newController.handleInput("undo") // Sollte wieder zu "Player1" zurückkehren
      newController.getCurrentPlayer.name shouldEqual "Player1"
    }

    "correctly handle player index wrap-around on undo" in {
      val newController = new Controller(jsonProvider, xmlProvider)
      newController.addPlayer("Player1")
      newController.addPlayer("Player2")
      newController.addPlayer("Player3")
      newController.handleInput("undo") // Aktueller Spieler ist "Player1", wrap-around sollte zu "Player3" zurückkehren
      newController.getCurrentPlayer.name shouldEqual "Player3"
    }

    "delegate input handling to current state" in {
      val dummyState = new DummyState
      controller.setState(dummyState)
      controller.handleInput("someInput")
      dummyState.handleInputCalled shouldEqual true
    }
  }
}
