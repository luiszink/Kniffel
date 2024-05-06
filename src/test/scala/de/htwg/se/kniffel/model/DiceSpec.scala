package de.htwg.se.kniffel.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.kniffel.model.Dice


class DiceTest extends AnyWordSpec with Matchers {
  "A Dice" when {
    "newly created" should {
      "have 5 random dice values each between 1 and 6" in {
        val dice = new Dice()
        dice.values should have size 5
        dice.values.foreach { d =>
          d should (be >= 1 and be <= 6)
        }
      }
    }

    "keeping dice" should {
      "keep the correct dice and reroll others" in {
        val initialDice = Dice(List(3, 3, 5, 6, 2))
        val keptIndices = List(1, 4, 5)
        val newDice = initialDice.keepDice(keptIndices)
        newDice.values.count(d => List(3, 6, 2).contains(d)) should be >= 3
      }
    }
  }
}
