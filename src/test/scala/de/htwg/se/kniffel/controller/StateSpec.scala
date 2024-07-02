package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.util._
import de.htwg.se.kniffel.controller.controllerImpl._
import de.htwg.se.kniffel.model.modelImpl._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// Dummy implementation of Controller
class TestController extends ControllerInterface {
  var currentPlayer: PlayerInterface = Player("TestPlayer")
  var keptDice: List[Int] = List()
  var scoreUpdated: Boolean = false
  var playerChanged: Boolean = false

  override def getCurrentPlayer: PlayerInterface = currentPlayer
  override def updateScore(category: String): Unit = {
    scoreUpdated = true
  }
  override def nextPlayer(): Unit = {
    playerChanged = true
  }
  override def keepDice(dice: List[Int]): Unit = {
    keptDice = dice
  }

  // Implement other abstract members of ControllerInterface with default values
  override def repetitions: Int = 2
  override def getDice: List[Int] = List(1, 2, 3, 4, 5)
  override def getPreviousDice: List[Int] = List()
  override def getCurrentState: StateInterface = new RollingState()
  override def getPlayers: List[PlayerInterface] = List(currentPlayer)
  override def addPlayer(name: String): Unit = {}
  override def setScoreUpdater(userInput: String): Unit = {}
  override def setState(state: StateInterface): Unit = {}
  override def handleInput(input: String): Unit = {}
  override def saveCurrentState(): Unit = {}
  override def getWinner: String = "TestPlayer"
  override def getFinalScores: List[String] = List("TestPlayer: 100")
}

class StateSpec extends AnyWordSpec with Matchers {

  "A RollingState" should {
    "return the correct name" in {
      val state = new RollingState
      state.name should be("RollingState")
    }

    "update the score and change the player when input is a score card category" in {
      val controller = new TestController
      controller.getCurrentPlayer.scoreCard.categories.update("ones", None) // Adding the category to the player's score card
      val state = new RollingState
      state.handleInput("ones", controller)

      controller.scoreUpdated should be(true)
      controller.playerChanged should be(true)
    }

    "keep the dice when input is not a score card category" in {
      val controller = new TestController
      val state = new RollingState
      state.handleInput("1 2 3", controller)

      controller.keptDice should be(List(1, 2, 3))
    }
  }

  "An UpdateState" should {
    "return the correct name" in {
      val state = new UpdateState
      state.name should be("UpdateState")
    }

    "update the score and change the player on input" in {
      val controller = new TestController
      val state = new UpdateState
      state.handleInput("fours", controller)

      controller.scoreUpdated should be(true)
      controller.playerChanged should be(true)
    }
  }
}
