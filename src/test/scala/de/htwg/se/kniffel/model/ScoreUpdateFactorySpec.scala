package de.htwg.se.kniffel.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ScoreUpdaterFactorySpec extends AnyWordSpec with Matchers {

  "A ScoreUpdaterFactory" should {

    "create a StandardScoreUpdater when input is 'y'" in {
      val updater = ScoreUpdaterFactory.createScoreUpdater("y")
      updater shouldBe a[StandardScoreUpdater]
    }

    "create a SpecialScoreUpdater when input is 'n'" in {
      val updater = ScoreUpdaterFactory.createScoreUpdater("n")
      updater shouldBe a[SpecialScoreUpdater]
    }

    "create a StandardScoreUpdater when input is 'standard'" in {
      val updater = ScoreUpdaterFactory.createScoreUpdater("standard")
      updater shouldBe a[StandardScoreUpdater]
    }

    "throw an IllegalArgumentException for invalid input" in {
      an[IllegalArgumentException] should be thrownBy {
        ScoreUpdaterFactory.createScoreUpdater("invalid")
      }
    }
  }

  "A StandardScoreUpdater" should {

    "update the score correctly for a valid category" in {
      val player = Player("TestPlayer", ScoreCard())
      val updater = new StandardScoreUpdater
      val dice = List(1, 1, 1, 2, 3)
      updater.updateScore(player, "one", dice)
      player.scoreCard.categories("one") shouldEqual Some(3)
    }

    "update the score correctly for all categories" in {
      val player = Player("TestPlayer", ScoreCard())
      val updater = new StandardScoreUpdater

      val testCases = Map(
        "two" -> (List(1, 2, 2, 4, 5), 4),
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
        updater.updateScore(player, category, dice)
        player.scoreCard.categories(category) shouldEqual Some(expectedScore)
      }
    }

    "not update the score for an invalid category" in {
      val player = Player("TestPlayer", ScoreCard())
      val updater = new StandardScoreUpdater
      val dice = List(1, 1, 1, 2, 3)
      an[IllegalArgumentException] should be thrownBy {
        updater.updateScore(player, "invalid", dice)
      }
    }

    "not update the score if the category is already filled" in {
      val player = Player("TestPlayer", ScoreCard())
      player.scoreCard.categories.update("one", Some(3))
      val updater = new StandardScoreUpdater
      val dice = List(1, 1, 1, 2, 3)
      updater.updateScore(player, "one", dice)
      player.scoreCard.categories("one") shouldEqual Some(3) // Score should remain unchanged
    }
  }

  "A SpecialScoreUpdater" should {

    "update the score correctly for a valid category" in {
      val player = Player("TestPlayer", ScoreCard())
      val updater = new SpecialScoreUpdater
      val dice = List(1, 1, 1, 2, 3)
      updater.updateScore(player, "one", dice)
      player.scoreCard.categories("one") shouldEqual Some(3)
    }

    "update the score correctly for all categories" in {
      val player = Player("TestPlayer", ScoreCard())
      val updater = new SpecialScoreUpdater

      val testCases = Map(
        "two" -> (List(1, 2, 2, 4, 5), 4),
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
        updater.updateScore(player, category, dice)
        player.scoreCard.categories(category) shouldEqual Some(expectedScore)
      }
    }

    "not update the score for an invalid category" in {
      val player = Player("TestPlayer", ScoreCard())
      val updater = new SpecialScoreUpdater
      val dice = List(1, 1, 1, 2, 3)
      an[IllegalArgumentException] should be thrownBy {
        updater.updateScore(player, "invalid", dice)
      }
    }

    "not update the score if the category is already filled" in {
      val player = Player("TestPlayer", ScoreCard())
      player.scoreCard.categories.update("one", Some(3))
      val updater = new SpecialScoreUpdater
      val dice = List(1, 1, 1, 2, 3)
      updater.updateScore(player, "one", dice)
      player.scoreCard.categories("one") shouldEqual Some(3) // Score should remain unchanged
    }
  }
}
