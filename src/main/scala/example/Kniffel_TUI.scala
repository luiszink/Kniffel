import scala.io.StdIn
import scala.util.Random
import example.Dice

object KniffelApp {

  // Method to display the current values of all dice
  def displayDiceValues(diceValues: List[Int]): Unit = {
    val horizontalLine =
      "+" + List.fill(diceValues.length)("---").mkString("+") + "+"
    val diceIconsLine =
      "|" + diceValues.map(value => s" $value ").mkString("|") + "|"
    val numCounter = " " + diceValues.indices
      .map(index => s" ${index + 1} ")
      .mkString(" ") + " "

    println(s"$horizontalLine\n$diceIconsLine\n$horizontalLine\n$numCounter");
  }

  def getInput(repetitions: Int): Option[List[Int]] = {
    // Start the game and display the final dice values
    println(
      s"Enter the indices of the dice you want to keep (e.g., 1 3 5), or type 'f' to end (${repetitions} remaining):"
    )
    val input = StdIn.readLine()

    if (input.toLowerCase == "f") {
      println("Ending the game...")
      None
    } else {
      Option(input.split(" ").map(_.toInt).toList)
    }
  }

  def main(args: Array[String]): Unit = {
    var dice = new Dice();
    var repetitions = 2
    var running = true

    displayDiceValues(dice.values)

    while (running) {
      val diceKeepIndizes = getInput(repetitions)
      diceKeepIndizes match {
        case Some(value) => {
          dice = dice.keepDice(diceKeepIndizes.get)
          displayDiceValues(dice.values)
          repetitions = repetitions - 1;
        }
        case None => running = false
      }
      if (repetitions == 0) {
        // TODO: Change to other player, reroll all dice
        running = false
      }
    }
  }
}
