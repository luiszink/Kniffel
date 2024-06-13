package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.model._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UpdateScoreCommandSpec extends AnyWordSpec with Matchers {

  "An UpdateScoreCommand" should {
    val player = Player("TestPlayer", ScoreCard())

    "calculate and update the score correctly when doStep is called" in {
      val dice = List(1, 1, 1, 4, 5)
      val command = new UpdateScoreCommand(player, "one", dice)
      command.doStep
      player.scoreCard.categories("one") shouldEqual Some(3)
    }

    "restore the previous score when undoStep is called" in {
      val dice = List(1, 1, 1, 4, 5)
      val command = new UpdateScoreCommand(player, "one", dice)
      command.doStep
      command.undoStep
      player.scoreCard.categories("one") shouldEqual Some(3)
    }

    "redo the score update correctly when redoStep is called" in {
      val dice = List(1, 1, 1, 4, 5)
      val command = new UpdateScoreCommand(player, "one", dice)
      command.doStep
      command.undoStep
      command.redoStep
      player.scoreCard.categories("one") shouldEqual Some(3)
    }

    "calculate and update the score correctly for all categories" in {
      val testCases = Map(
        "two" -> (List(2, 2, 3, 4, 5), 4),
        "three" -> (List(3, 3, 3, 4, 5), 9),
        "four" -> (List(4, 4, 4, 4, 5), 16),
        "five" -> (List(5, 5, 5, 5, 5), 25),
        "six" -> (List(6, 6, 6, 6, 6), 30),
        "threeofakind" -> (List(3, 3, 3, 4, 5), 18),
        "fourofakind" -> (List(4, 4, 4, 4, 5), 21),
        "fullhouse" -> (List(3, 3, 3, 5, 5), 25),
        "smallstraight" -> (List(1, 2, 3, 4, 6), 30),
        "largestraight" -> (List(2, 3, 4, 5, 6), 40),
        "chance" -> (List(1, 2, 3, 4, 5), 15),
        "kniffel" -> (List(6, 6, 6, 6, 6), 50)
      )

      for ((category, (dice, expectedScore)) <- testCases) {
        val player = Player("TestPlayer", ScoreCard())
        val command = new UpdateScoreCommand(player, category, dice)
        command.doStep
        player.scoreCard.categories(category) shouldEqual Some(expectedScore)
      }
    }

    "throw an exception for an invalid category" in {
      val player = Player("TestPlayer", ScoreCard())
      val dice = List(1, 1, 1, 4, 5)
      val invalidCommand = new UpdateScoreCommand(player, "invalid", dice)
      an [IllegalArgumentException] should be thrownBy invalidCommand.doStep
    }
  }
}
