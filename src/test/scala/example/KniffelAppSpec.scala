import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import scala.io.StdIn
import java.io.ByteArrayInputStream

class KniffelAppSpec extends AnyWordSpec {

  "The KniffelApp" should {
    "display the current values of all dice" in {
      val diceValues = List(1, 2, 3, 4, 5)
      val expectedOutput =
        """+---+---+---+---+---+
          || 1 | 2 | 3 | 4 | 5 |
          |+---+---+---+---+---+
          |  1   2   3   4   5  """.stripMargin.trim

      val outputStream = new java.io.ByteArrayOutputStream()
      Console.withOut(outputStream) {
        KniffelApp.displayDiceValues(diceValues)
      }
      val printedOutput = outputStream.toString.trim

      // Bereinigen der erwarteten und tatsächlichen Ausgabe von Leerzeichen und Zeilenumbrüchen
      val cleanedExpectedOutput = expectedOutput.replaceAll("\\s", "")
      val cleanedPrintedOutput = printedOutput.replaceAll("\\s", "")

      cleanedPrintedOutput shouldBe cleanedExpectedOutput
    }

    "get input from the user" in {
      val input = "1 3 5\n"
      val inputStream = new ByteArrayInputStream(input.getBytes)
      Console.withIn(inputStream) {
        val result = KniffelApp.getInput(2)
        result shouldBe Some(List(1, 3, 5))
      }
    }
  }
}
