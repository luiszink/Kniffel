import scala.io.StdIn
import scala.util.Random

object KniffelApp {
  // Method to roll a single dice and return its icon as a string
  def rollDice(): String = {
    String.valueOf(Random.nextInt(6) + 1)
  }

  // Method to display the current values of all dice
  def displayDiceValues(diceValues: List[String]): Unit = {
    val horizontalLine = "+" + List.fill(diceValues.length)("---").mkString("+") + "+"
    val diceIconsLine = "|" + diceValues.map(value => s" $value ").mkString("|") + "|"
    val numCounter = " " + diceValues.indices.map(index => s" ${index + 1} ").mkString(" ") + " "

    println(horizontalLine)
    println(diceIconsLine)
    println(horizontalLine)
    println(numCounter)
  }

  // Method to keep selected dice and return them, also rerolling the rest
  def keepDice(diceValues: List[String], keepIndices: List[Int]): (List[String], List[String]) = {
    val (keptDice, _) = diceValues.zipWithIndex.partition { case (_, index) => keepIndices.contains(index + 1) }
    val newDice = diceValues.diff(keptDice.map(_._1))
    val rerolledDice = List.fill(newDice.length)(rollDice())
    (keptDice.map(_._1), keptDice.map(_._1) ++ rerolledDice)
  }

  def main(args: Array[String]): Unit = {
    // Roll the dice and display their values
    val diceValues = List.fill(5)(rollDice())
    displayDiceValues(diceValues)

    // Ask the user which dice to keep
    println("Enter the indices of the dice you want to keep (e.g., 1 3 5):")
    val input = StdIn.readLine()
    val keepIndices = input.split(" ").map(_.toInt).toList

    // Keep the selected dice and display them
    val (keptDice, finalDiceValues) = keepDice(diceValues, keepIndices)
    displayDiceValues(keptDice)
    displayDiceValues(finalDiceValues)
  }
}
