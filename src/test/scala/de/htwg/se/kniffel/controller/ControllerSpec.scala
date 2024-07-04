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
import scala.util.{Try, Success, Failure}
import java.io.ByteArrayOutputStream
import java.io.PrintStream

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

    "add players correctly" in {
      val controller = new Controller(jsonProvider, xmlProvider)
      controller.addPlayer("Player1")
      controller.addPlayer("Player2")
      controller.getCurrentPlayer.name shouldEqual "Player1"
      controller.nextPlayer()
      controller.getCurrentPlayer.name shouldEqual "Player2"
    }

    "return the correct current player" in {
      val controller = new Controller(jsonProvider, xmlProvider)
      controller.addPlayer("Player1")
      controller.addPlayer("Player2")
      controller.getCurrentPlayer.name shouldEqual "Player1"
    }

    "switch to the next player correctly" in {
      val controller = new Controller(jsonProvider, xmlProvider)
      controller.addPlayer("Player1")
      controller.addPlayer("Player2")
      controller.nextPlayer()
      controller.getCurrentPlayer.name shouldEqual "Player2"
    }

    "keep dice correctly" in {
      val controller = new Controller(jsonProvider, xmlProvider)
      val initialDice = controller.getDice
      controller.keepDice(List(1, 2, 3))
      val newDice = controller.getDice
      newDice should not equal initialDice
      newDice.slice(0, 3) shouldEqual initialDice.slice(0, 3)
    }

    "notify observers correctly when keeping dice" in {
      val controller = new Controller(jsonProvider, xmlProvider)
      val observer = mock[Observer]
      controller.add(observer)
      controller.keepDice(List(1, 2, 3))
      verify(observer, atLeastOnce()).update(KniffelEvent.keepDice)
    }

    "change state and notify observers correctly when repetitions are zero" in {
      val controller = spy(new Controller(jsonProvider, xmlProvider))
      controller.repetitions = 1
      controller.keepDice(List(1, 2, 3))
      controller.getCurrentState shouldBe a[UpdateState]
      verify(controller, atLeastOnce()).notifyObservers(
        KniffelEvent.noRepetitions
      )
    }

    "get previous dice correctly" in {
      val controller = new Controller(jsonProvider, xmlProvider)
      controller.addPlayer(
        "Player1"
      ) // Sicherstellen, dass mindestens ein Spieler vorhanden ist

      // Case 1: previousDice is defined
      val diceValues = controller.getDice
      controller.nextPlayer()
      controller.getPreviousDice shouldEqual diceValues

      // Case 2: previousDice is None
      val newController = new Controller(jsonProvider, xmlProvider)
      newController.addPlayer(
        "Player1"
      ) // Sicherstellen, dass mindestens ein Spieler vorhanden ist
      newController.getPreviousDice shouldEqual List()
    }
    "handle state transitions correctly" in {
      val controller = new Controller(jsonProvider, xmlProvider)
      controller.setState(new RollingState())
      controller.getCurrentState shouldBe a[RollingState]
      controller.setState(new UpdateState())
      controller.getCurrentState shouldBe a[UpdateState]
    }

    "handle score updater correctly" in {
      val controller = new Controller(jsonProvider, xmlProvider)
      controller.setScoreUpdater("n")
      controller.isStandardScoreUpdater shouldEqual true
      controller.setScoreUpdater("y")
      controller.isMultiKniffelScoreUpdater shouldEqual true
    }

    "delegate input handling to current state" in {
      val controller = new Controller(jsonProvider, xmlProvider)
      val dummyState = new DummyState
      controller.setState(dummyState)
      controller.handleInput("someInput")
      dummyState.handleInputCalled shouldEqual true
    }

    // Neue Tests für ungetestete Funktionen

    "roll dice correctly" in {
      val controller = new Controller(jsonProvider, xmlProvider)
      val initialDice = controller.getDice
      controller.rollDice()
      val newDice = controller.getDice
      newDice should not equal initialDice
    }

    "return the correct winner" in {
      val controller = new Controller(jsonProvider, xmlProvider)
      controller.addPlayer("Player1")
      controller.addPlayer("Player2")
      // Annahme: Score-Kategorien sind nicht null und Spieler 1 hat höhere Punktzahl
      controller.getPlayers.head.scoreCard.categories
        .update("totalScore", Some(100))
      controller
        .getPlayers(1)
        .scoreCard
        .categories
        .update("totalScore", Some(50))
      controller.getWinner shouldEqual "Player1"
    }

    "return the correct final scores" in {
      val controller = new Controller(jsonProvider, xmlProvider)
      controller.addPlayer("Player1")
      controller.addPlayer("Player2")

      val player1 = controller.getPlayers.head
      val player2 = controller.getPlayers(1)

      // Alle Kategorien für beide Spieler ausfüllen
      player1.scoreCard.categories.keys.foreach { key =>
        player1.scoreCard.categories.update(key, Some(5))
      }
      player2.scoreCard.categories.keys.foreach { key =>
        player2.scoreCard.categories.update(key, Some(3))
      }

      // Total Score berechnen
      player1.scoreCard.calculateTotalScore()
      player2.scoreCard.calculateTotalScore()

      controller.getFinalScores shouldEqual List("Player1: 65", "Player2: 39")
    }

    "check game end correctly" in {
      val controller = spy(new Controller(jsonProvider, xmlProvider))
      controller.addPlayer("Player1")
      controller.addPlayer("Player2")
      val player1 = controller.getPlayers.head
      val player2 = controller.getPlayers(1)

      // Füllen aller Kategorien für beide Spieler
      player1.scoreCard.categories.keys.foreach { key =>
        player1.scoreCard.categories.update(key, Some(1))
      }
      player2.scoreCard.categories.keys.foreach { key =>
        player2.scoreCard.categories.update(key, Some(1))
      }

      controller.checkGameEnd()
      // Überprüfen, ob das Event `GameEnded` ausgelöst wurde
      verify(controller, times(1)).notifyObservers(KniffelEvent.GameEnded)
    }

    "update score correctly" in {
      val controller = new Controller(jsonProvider, xmlProvider)
      controller.addPlayer("Player1")
      controller.rollDice()
      controller.updateScore("one")
      // Überprüfen, ob die Punktzahl aktualisiert wurde
      controller.getCurrentPlayer.scoreCard.categories(
        "one"
      ) should not be empty
    }

    "update score and calculate total score correctly" in {
      val controller = new Controller(jsonProvider, xmlProvider)
      controller.addPlayer("Player1")

      // Mocking the score categories to ensure isComplete returns true
      val player = controller.getCurrentPlayer
      player.scoreCard.categories.keys.foreach { key =>
        if (
          key != "totalScore" && key != "bonus" && key != "upperSectionScore" && key != "lowerSectionScore"
        ) {
          player.scoreCard.categories.update(key, Some(5))
        }
      }

      controller.rollDice()
      controller.updateScore("one")

      // Capturing the console output
      val outCapture = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outCapture)) {
        player.scoreCard.calculateTotalScore()
      }

      // Verify total score is calculated
      player.scoreCard.categories("totalScore") should not be empty
    }

    "handle input and notify observers correctly" in {
      val controller = new Controller(jsonProvider, xmlProvider)
      val observer = mock[Observer]
      controller.add(observer)
      controller.handleInput("undo")
      verify(observer, atLeastOnce()).update(KniffelEvent.Undo)
      controller.handleInput("invalidInput")
      verify(observer, atLeastOnce()).update(KniffelEvent.InvalidInput)
    }
  }
}
