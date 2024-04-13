import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class KniffelAppSpec extends AnyWordSpec {

  "The KniffelApp" should {
    "roll a single dice and return its icon as a string" in {
      val diceIcon = KniffelApp.rollDice()
      assert(List("1", "2", "3", "4", "5", "6").contains(diceIcon))
    }

    "display the current values of all dice" in {
      val numDice = 5 // Anzahl der Würfel
      val diceValues = List.fill(numDice)(KniffelApp.rollDice())
      val expectedOutput = generateExpectedOutput(diceValues)
      val output = captureOutput {
        KniffelApp.displayDiceValues(diceValues)
      }
      assert(output.trim == expectedOutput)
    }

    "run the main method without errors" in {
      noException should be thrownBy KniffelApp.main(Array.empty)
    }
  }

  // Helper method to generate the expected output based on the dice values
  def generateExpectedOutput(diceValues: List[String]): String = {
    val horizontalLine = "+" + List.fill(diceValues.length)("---").mkString("+") + "+"
    val diceIconsLine = "|" + diceValues.map(value => s" $value ").mkString("|") + "|"
    s"""
       |$horizontalLine
       |$diceIconsLine
       |$horizontalLine
     """.stripMargin.trim
  }

  // Helper method to capture console output
  def captureOutput(block: => Unit): String = {
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream)(block)
    stream.toString
  }
}
