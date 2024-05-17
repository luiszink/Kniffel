package de.htwg.se.kniffel.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

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