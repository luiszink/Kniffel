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

  // Recursive method to reroll the dice with a specified number of repetitions
  def reroll(diceValues: List[String], repetitions: Int): List[String] = {
    // Ask the user which dice to keep
  println(s"Enter the indices of the dice you want to keep (e.g., 1 3 5), or type 'f' to end (${repetitions} remaining):")
  val input = StdIn.readLine()

  if (repetitions <= 1 || input.toLowerCase == "f") {
    println("Final dice values:")
    diceValues // Return the final dice values
  } else {
    if (input.toLowerCase == "f") {
      println("Ending the game...")
      diceValues // Return the final dice values
    } else {
      val keepIndices = input.split(" ").map(_.toInt).toList

      // Keep the selected dice and display them
      val (_, finalDiceValues) = keepDice(diceValues, keepIndices)
      displayDiceValues(finalDiceValues)

      // Continue playing recursively with one less repetition
      reroll(finalDiceValues, repetitions - 1)
  }
}

  }

  def main(args: Array[String]): Unit = {
    // Roll the dice and display their values
    val diceValues = List.fill(5)(rollDice())
    displayDiceValues(diceValues)
    
    // Times to roll the dice again (normal 2)
    val repetitions = 2

    // Start the game and display the final dice values
    displayDiceValues(reroll(diceValues, repetitions))
  }
}
