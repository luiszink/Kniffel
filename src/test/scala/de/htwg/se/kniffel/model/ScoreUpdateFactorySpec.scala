package de.htwg.se.kniffel.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.kniffel.model.Player


class ScoreUpdaterFactorySpec extends AnyWordSpec with Matchers {

  "A ScoreUpdaterFactory" should {

    "return a StandardScoreUpdater for 'y'" in {
      val scoreUpdater = ScoreUpdaterFactory.createScoreUpdater("y")
      scoreUpdater shouldBe a[StandardScoreUpdater]
    }

    "return a SpecialScoreUpdater for 'n'" in {
      val scoreUpdater = ScoreUpdaterFactory.createScoreUpdater("n")
      scoreUpdater shouldBe a[SpecialScoreUpdater]
    }

    "throw an IllegalArgumentException for invalid input" in {
      assertThrows[IllegalArgumentException] {
        ScoreUpdaterFactory.createScoreUpdater("invalid")
      }
    }
  }
}

  class StandardScoreUpdaterSpec extends AnyWordSpec with Matchers {

      "not update a category if it is already filled" in {
        val player = Player("TestPlayer")
        val scoreCard = ScoreCard()
        player.scoreCard.categories = scoreCard.categories.clone()
        val updater = new StandardScoreUpdater()

        // Fill a category first
        player.scoreCard.categories("one") = Some(5)

        // Try to update the filled category
        updater.updateScore(player, "one", List(1, 1, 1, 1, 1))

        // Check if the category remains unchanged
        player.scoreCard.categories("one").get should be (5)
      }

      "throw an IllegalArgumentException for an invalid category" in {
        val player = Player("TestPlayer")
        val scoreCard = ScoreCard()
        player.scoreCard.categories = scoreCard.categories.clone()
        val updater = new StandardScoreUpdater()

        // Try to update with an invalid category
        assertThrows[IllegalArgumentException] {
          updater.updateScore(player, "invalidCategory", List(1, 2, 3, 4, 5))
      }
    }
  }
class SpecialScoreUpdaterSpec extends AnyWordSpec with Matchers {


    "not update a category if it is already filled" in {
      val player = Player("TestPlayer")
      val scoreCard = ScoreCard()
      player.scoreCard.categories = scoreCard.categories.clone()
      val updater = new SpecialScoreUpdater()

      // Fill a category first
      player.scoreCard.categories("one") = Some(5)

      // Try to update the filled category
      updater.updateScore(player, "one", List(1, 1, 1, 1, 1))

      // Check if the category remains unchanged
      player.scoreCard.categories("one").get should be (5)
    }

    "throw an IllegalArgumentException for an invalid category" in {
      val player = Player("TestPlayer")
      val scoreCard = ScoreCard()
      player.scoreCard.categories = scoreCard.categories.clone()
      val updater = new SpecialScoreUpdater()

      // Try to update with an invalid category
      assertThrows[IllegalArgumentException] {
        updater.updateScore(player, "invalidCategory", List(1, 2, 3, 4, 5))
      
    }
  }
}