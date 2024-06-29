package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.model.{Player, ScoreCard}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// Dummy implementation of Controller
class TestController extends Controller {
  var currentPlayer: Player = new Player("TestPlayer", new ScoreCard)
  var keptDice: List[Int] = List()
  var scoreUpdated: Boolean = false
  var playerChanged: Boolean = false

  override def getCurrentPlayer: Player = currentPlayer
  override def updateScore(category: String): Unit = {
    scoreUpdated = true
  }
  override def nextPlayer(): Unit = {
    playerChanged = true
  }
  override def keepDice(dice: List[Int]): Unit = {
    keptDice = dice
  }
}

class StateSpec extends AnyWordSpec with Matchers {

  "A RollingState" should {
    "return the correct name" in {
      val state = new RollingState
      state.name should be("RollingState")
    }

    "update the score and change the player when input is a score card category" in {
      val controller = new TestController
      controller.currentPlayer.scoreCard.categories.update("ones", None) // Adding the category to the player's score card
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
      state.name should be("updateState")
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
