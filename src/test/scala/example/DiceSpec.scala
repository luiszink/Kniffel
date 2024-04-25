import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import example.Dice

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
      
      // Überprüfe, ob alle behaltenen Würfelwerte in der neuen Würfelinstanz vorhanden sind
      val keptDiceValues = newDice.values.slice(0, diceToKeep.length)
      keptDiceValues should contain theSameElementsAs List(1, 3, 5)
      
      // Überprüfe, ob die Anzahl der Würfelwerte in der neuen Würfelinstanz korrekt ist
      newDice.values should have length 5

      val afaskdfj = new Dice()
      afaskdfj.values should have length 5


    }
  }
}