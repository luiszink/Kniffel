package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.controller.controllerImpl._
import de.htwg.se.kniffel.model.fileIoComponents.FileIoInterface
import de.htwg.se.kniffel.model.fileIoComponents.fileIoJsonImpl.FileIoJsonImpl
import de.htwg.se.kniffel.model.fileIoComponents.fileIoXmlImpl.FileIoXmlImpl
import com.google.inject.{Inject, Provider}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.kniffel.util.{Observable, Observer, KniffelEvent}

class DummyState extends StateInterface {
  var handleInputCalled = false
  override def name: String = "DummyState"
  override def handleInput(
      input: String,
      controller: ControllerInterface
  ): Unit = {
    handleInputCalled = true
  }
}

class ControllerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A Controller" should {
    val jsonProvider = mock[Provider[FileIoJsonImpl]]
    val xmlProvider = mock[Provider[FileIoXmlImpl]]
    val fileIoJsonMock = mock[FileIoJsonImpl]
    val fileIoXmlMock = mock[FileIoXmlImpl]

    when(jsonProvider.get()).thenReturn(fileIoJsonMock)
    when(xmlProvider.get()).thenReturn(fileIoXmlMock)

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

    "handle state transitions correctly" in {
      controller.setState(new RollingState())
      controller.getCurrentState shouldBe a[RollingState]
      controller.setState(new UpdateState())
      controller.getCurrentState shouldBe a[UpdateState]
    }

    "handle score updater correctly" in {
      controller.setScoreUpdater("n")
    }

    "delegate input handling to current state" in {
      val dummyState = new DummyState
      controller.setState(dummyState)
      controller.handleInput("someInput")
      dummyState.handleInputCalled shouldEqual true
    }

    // Neue Tests für ungetestete Funktionen

    "roll dice correctly" in {
      val initialDice = controller.getDice
      controller.rollDice()
      val newDice = controller.getDice
      newDice should not equal initialDice
    }

    "return the correct winner" in {
      val newController = new Controller(jsonProvider, xmlProvider)
      newController.addPlayer("Player1")
      newController.addPlayer("Player2")
      // Annahme: Score-Kategorien sind nicht null und Spieler 1 hat höhere Punktzahl
      newController.getPlayers.head.scoreCard.categories
        .update("totalScore", Some(100))
      newController
        .getPlayers(1)
        .scoreCard
        .categories
        .update("totalScore", Some(50))
      newController.getWinner shouldEqual "Player1"
    }

    "return the correct final scores" in {
      val newController = new Controller(jsonProvider, xmlProvider)
      newController.addPlayer("Player1")
      newController.addPlayer("Player2")
      newController.getPlayers.head.scoreCard.categories
        .update("totalScore", Some(100))
      newController
        .getPlayers(1)
        .scoreCard
        .categories
        .update("totalScore", Some(50))
      newController.getFinalScores shouldEqual List("Player1: 0", "Player2: 0")
    }

    "check game end correctly" in {
      val controllerMock = spy(new Controller(jsonProvider, xmlProvider))
      controllerMock.addPlayer("Player1")
      controllerMock.addPlayer("Player2")
      val player1 = controllerMock.getPlayers.head
      val player2 = controllerMock.getPlayers(1)

      // Füllen aller Kategorien für beide Spieler
      player1.scoreCard.categories.keys.foreach { key =>
        player1.scoreCard.categories.update(key, Some(1))
      }
      player2.scoreCard.categories.keys.foreach { key =>
        player2.scoreCard.categories.update(key, Some(1))
      }

      controllerMock.checkGameEnd()
      // Überprüfen, ob das Event `GameEnded` ausgelöst wurde
      verify(controllerMock, times(1)).notifyObservers(KniffelEvent.GameEnded)
    }

    "update score correctly" in {
      val newController = new Controller(jsonProvider, xmlProvider)
      newController.addPlayer("Player1")
      newController.rollDice()
      newController.updateScore("one")
      // Überprüfen, ob die Punktzahl aktualisiert wurde
      newController.getCurrentPlayer.scoreCard.categories(
        "one"
      ) should not be empty
    }
  }
}
