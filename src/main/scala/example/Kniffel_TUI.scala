import scala.util.Random

object KniffelApp {
  // Method to roll a single dice and return its icon as a string
  def rollDice(): String = {
    val randomNumber = Random.nextInt(6) + 1
    List("1", "2", "3", "4", "5", "6")(randomNumber - 1)
  }

  // Method to display the current values of all dice
  def displayDiceValues(diceValues: List[String]): Unit = {
    val horizontalLine = "+" + List.fill(diceValues.length)("---").mkString("+") + "+"
    val diceIconsLine = "|" + diceValues.map(value => s" $value ").mkString("|") + "|"

    println(horizontalLine)
    println(diceIconsLine)
    println(horizontalLine)
  }

  def main(args: Array[String]): Unit = {
    // Roll the dice and display their values
    val diceValues = List.fill(5)(rollDice())
    displayDiceValues(diceValues)
  }
}