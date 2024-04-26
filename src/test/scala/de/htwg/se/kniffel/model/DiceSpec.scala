package de.htwg.se.kniffel.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.kniffel.model.Dice


class DiceSpec extends AnyWordSpec {

  "The Dice class" should {
    "roll a single dice and return its icon as an integer" in {
      val diceIcon = Dice.rollDice()
      diceIcon should (be >= 1 and be <= 6)
    }

    "keep specified dice and reroll the rest" in {
      val dice = Dice(List(1, 2, 3, 4, 5))
      val diceToKeep = List(1, 3, 5)
      val newDice = dice.keepDice(diceToKeep)
      
      // Check if all kept dice values are present in the new dice instance
      val keptDiceValues = newDice.values.slice(0, diceToKeep.length)
      keptDiceValues should contain theSameElementsAs List(1, 3, 5)
      
      // Check if the number of dice values in the new dice instance is correct
      newDice.values should have length 5

      val initialDice = new Dice()
      initialDice.values should have length 5


    }
  }
}
