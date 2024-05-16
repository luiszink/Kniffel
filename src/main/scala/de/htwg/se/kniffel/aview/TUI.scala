package de.htwg.se.kniffel.aview

import scala.io.StdIn
import de.htwg.se.kniffel.util.Observer
import de.htwg.se.kniffel.controller.Controller
import de.htwg.se.kniffel.model.Player

class TUI(controller: Controller) extends Observer {
  controller.add(this)

  def input(): Option[List[Int]] = {
    var remainingAttempts = 2 // Maximale Anzahl von Wiederholungsversuchen
    
    // Retry Strategy Pattern
    while (remainingAttempts > 0) {
      println(
        s"Enter the indices of the dice you want to keep (e.g., 1 3 5), or type 'f' to end (${controller.repetitions} remaining):"
      )
      val input = StdIn.readLine()

      if (input.toLowerCase == "f") {
        updateScore()
        controller.nextPlayer()
        return None
      } else {
        // Attempt to parse user input into a list of integers
        try {
          val diceToKeep = input.split(" ").map(_.toInt).toList
          return Some(diceToKeep)
          // Decrement remaining attempts
          remainingAttempts -= 1          
        } catch {
          case e: NumberFormatException =>
            println("Invalid input! Please enter valid indices.")
        }
      }
    }
    println("Maximum number of attempts reached. Ending the game...")
    None
  }

  def printDice() = {
    val diceValues: List[Int] = controller.getDice
    val horizontalLine =
      "+" + List.fill(diceValues.length)("---").mkString("+") + "+"
    val diceIconsLine =
      "|" + diceValues.map(value => s" $value ").mkString("|") + "|"
    val numCounter = " " + diceValues.indices
      .map(index => s" ${index + 1} ")
      .mkString(" ") + " "

    s"Current Player: ${controller.getCurrentPlayer}\n$horizontalLine\n$diceIconsLine\n$horizontalLine\n$numCounter"
  }

  def printScoreCard() = {
    val currentPlayer = controller.getCurrentPlayer
    val scoreCard = currentPlayer.scoreCard.categories.map {case (category, score) => s"$category: ${score.getOrElse("_")}"}.mkString("\n")
    s"Current Player: ${currentPlayer.name}\n\nScoreCard:\n$scoreCard"
  }

  def printGame() = {
    s"\n\n\n${printScoreCard()}\n\n${printDice()}"
  }

  def addPlayers(): Unit = {
    println("Enter player names (comma-separated):")
    val input = StdIn.readLine()
    val names = input.split(",").map(_.trim)
    names.foreach(controller.addPlayer)
  }

  def updateScore(): Unit = {
    println("Enter category and score (e.g., One, Fullhouse...:")
    val input = StdIn.readLine()
    try{
      controller.updateScore(input)
    }
    catch{
      case e: IllegalArgumentException =>
        println("Not a catagory! Please try again.")
        updateScore()
    }
  }

  var running = true
  
  def run() = {
    addPlayers()
    println(printDice())
    while (running) {
      input() match {
        case Some(value) => controller.keepDice(value)
        case None => controller.nextPlayer()
      }
    }
  }

  override def update(message: String): Unit = {
    message match {
      case "printDice" => println(printDice())
      case "printScoreCard" => println(printScoreCard())
      case "playerAdded" => println("")
      case "updateScore" => updateScore()
      case _ => println(printGame())
    }
  }

}
