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
  }
}
