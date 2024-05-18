// TUI.scala
package de.htwg.se.kniffel.aview

import scala.io.StdIn
import de.htwg.se.kniffel.controller.Controller
import de.htwg.se.kniffel.util.Observer
import scala.util.{Try, Success, Failure}


class TUI(controller: Controller) extends Observer {
  controller.add(this)

  def addPlayers(): Unit = {
    println("Enter player names (comma-separated):")
    val input = StdIn.readLine()
    val names = input.split(",").map(_.trim)
    names.foreach(controller.addPlayer)
  }

  def run() = {
    addPlayers()
    println(printDice())
    var running = true
     while (running) {
      // maybe a try catch
      controller.getCurrentState match {
        case "new ScoringState" => printGame()
        case _ =>
          println(s"Enter the indices of the dice you want to keep (e.g., 1 3 5), or Enter category (e.g., One, Fullhouse...) (${controller.repetitions} remaining):")
          val input = StdIn.readLine()
          controller.handleInput(input)
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

  def printDice() = {
    val diceValues: List[Int] = controller.getDice
    val horizontalLine = "+" + List.fill(diceValues.length)("---").mkString("+") + "+"
    val diceIconsLine = "|" + diceValues.map(value => s" $value ").mkString("|") + "|"
    val numCounter = " " + diceValues.indices.map(index => s" ${index + 1} ").mkString(" ") + " "

    s"Current Player: ${controller.getCurrentPlayer}\n$horizontalLine\n$diceIconsLine\n$horizontalLine\n$numCounter"
  }

  def printScoreCard() = {
    val currentPlayer = controller.getCurrentPlayer
    val scoreCard = currentPlayer.scoreCard.categories.map {
      case (category, score) => s"$category: ${score.getOrElse("_")}"
    }.mkString("\n")
    s"Current Player: ${currentPlayer.name}\n\nScoreCard:\n$scoreCard"
  }

  def printGame() = {
    s"\n\n\n${printScoreCard()}\n\n${printDice()}"
  }

  def updateScore(): Unit = {
    println("Enter category (e.g., One, Fullhouse...):")
    val input = StdIn.readLine()
    Try(controller.updateScore(input)) match {
      case Success(_) => // Nichts weiter tun, wenn erfolgreich
      case Failure(e: IllegalArgumentException) =>
        println("Not a category! Please try again.")
        updateScore()
    }
  }

  def displayMessage(message: String): Unit = {
    println(message)
  }
}
