package de.htwg.se.kniffel.model.modelImpl

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.kniffel.model.scoreUpdaterImpl.{
  StandardScoreUpdater,
  MultiKniffelScoreUpdater
}

class ScoreUpdaterFactorySpec extends AnyWordSpec with Matchers {

  "A ScoreUpdaterFactory" should {

    "create a MultiKniffelScoreUpdater when input is 'y'" in {
      val updater = ScoreUpdaterFactory.createScoreUpdater("y")
      updater shouldBe a[MultiKniffelScoreUpdater]
    }

    "create a StandardScoreUpdater when input is 'n'" in {
      val updater = ScoreUpdaterFactory.createScoreUpdater("n")
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
      an[IllegalArgumentException] should be thrownBy {
        updater.updateScore(player, "one", dice)
      }
    }
  }

  "A MultiKniffelScoreUpdater" should {

    "update the score correctly for a valid category" in {
      val player = Player("TestPlayer", ScoreCard())
      val updater = new MultiKniffelScoreUpdater
      val dice = List(1, 1, 1, 2, 3)
      updater.updateScore(player, "one", dice)
      player.scoreCard.categories("one") shouldEqual Some(3)
    }

    "update the score correctly for all categories" in {
      val player = Player("TestPlayer", ScoreCard())
      val updater = new MultiKniffelScoreUpdater

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
      val updater = new MultiKniffelScoreUpdater
      val dice = List(1, 1, 1, 2, 3)
      an[IllegalArgumentException] should be thrownBy {
        updater.updateScore(player, "invalid", dice)
      }
    }

    "not update the score if the category is already filled" in {
      val player = Player("TestPlayer", ScoreCard())
      player.scoreCard.categories.update("one", Some(3))
      val updater = new MultiKniffelScoreUpdater
      val dice = List(1, 1, 1, 2, 3)
      an[IllegalArgumentException] should be thrownBy {
        updater.updateScore(player, "one", dice)
      }
    }

    "assign additional Kniffel points to a random empty category if kniffel category is already filled" in {
      val player = Player("TestPlayer", ScoreCard())
      val updater = new MultiKniffelScoreUpdater

      // Fill the kniffel category
      player.scoreCard.categories.update("kniffel", Some(50))

      // Dice result that would normally go into kniffel category
      val dice = List(6, 6, 6, 6, 6)

      // Capture the initial empty categories excluding bonus and total scores
      val emptyCategories =
        player.scoreCard.categories.filter(_._2.isEmpty).keys.toList
      val emptyCategoriesExcluding = emptyCategories.filterNot(cat =>
        cat == "bonus" || cat == "uppersectionscore" || cat == "totalscore"
      )

      // Update the score
      updater.updateScore(player, "kniffel", dice)

      // Verify that an additional 50 points have been added to one of the empty categories
      val updatedCategories = player.scoreCard.categories.filter {
        case (k, v) => emptyCategoriesExcluding.contains(k) && v.contains(50)
      }
      updatedCategories should have size 1
    }
  }
}
