package de.htwg.se.kniffel.aview

import scala.io.StdIn
import de.htwg.se.kniffel.controller.ControllerInterface
import de.htwg.se.kniffel.util.{Observer, KniffelEvent}
import scala.util.{Try, Success, Failure}
import com.google.inject.Inject

class TUI @Inject() (controller: ControllerInterface) extends Observer {
  controller.add(this)

  def addPlayers(): Unit = {
    println("Enter player names (comma-separated):")
    val input = StdIn.readLine()
    Try {
      val names = input.split(",").map(_.trim)
      if (names.isEmpty || names.exists(_.isEmpty)) {
        throw new IllegalArgumentException("Invalid input! Please enter at least one name, separated by commas.")
      }
      names.foreach(controller.addPlayer)
    } match {
      case Success(_) =>
      case Failure(_) =>
        println("Invalid input! Please try again.")
        addPlayers()
    }
  }

  def selectScoreUpdater(): Unit = {
    println("Do you want to allow multiple Kniffel? (y/n)")
    val input = StdIn.readLine()
    Try {
      controller.setScoreUpdater(input)
    } match {
      case Success(_) =>
      case Failure(_) =>
        println("Invalid input! Please try again.")
        selectScoreUpdater()
    }
  }

  def run() = {
    addPlayers()
    selectScoreUpdater()
    println(printDice())
    var running = true
    while (running) {
      controller.getCurrentState.name match {
        case "UpdateState" =>
          printScoreCard()
          println("Enter category (e.g., One, Fullhouse...!):")
          val input = StdIn.readLine()
          controller.handleInput(input)
          printDice()
        case _ =>
          printDice()
          println(s"Enter the indices of the dice you want to keep (e.g., 1 3 5), or Enter category (e.g., One, Fullhouse...) (${controller.repetitions} remaining), or 'undo' to undo last score update:")
          val input = StdIn.readLine()
          controller.handleInput(input)
      }
    }
  }

  override def update(event: KniffelEvent.Value): Unit = {
    event match {
      case KniffelEvent.PrintDice      => println(printDice())
      case KniffelEvent.PrintDiceUndo  => println(printDiceUndo())
      case KniffelEvent.PrintScoreCard => println(printScoreCard())
      case KniffelEvent.PlayerAdded    => println("")
      case KniffelEvent.InvalidInput   => println("Invalid input! Please try again.")
      case _                           => println("Tui update")
    }
  }

  def printDiceUndo() = {
    val diceValues: List[Int] = controller.getPreviousDice
    val horizontalLine = "+" + List.fill(diceValues.length)("---").mkString("+") + "+"
    val diceIconsLine = "|" + diceValues.map(value => s" $value ").mkString("|") + "|"
    val numCounter = " " + diceValues.indices.map(index => s" ${index + 1} ").mkString(" ") + " "
    s"Current Player: ${controller.getCurrentPlayer}\n$horizontalLine\n$diceIconsLine\n$horizontalLine\n$numCounter\n"
  }

  def printDice() = {
    val diceValues: List[Int] = controller.getDice
    val horizontalLine = "+" + List.fill(diceValues.length)("---").mkString("+") + "+"
    val diceIconsLine = "|" + diceValues.map(value => s" $value ").mkString("|") + "|"
    val numCounter = " " + diceValues.indices.map(index => s" ${index + 1} ").mkString(" ") + " "
    s"Current Player: ${controller.getCurrentPlayer}\n$horizontalLine\n$diceIconsLine\n$horizontalLine\n$numCounter\n"
  }

  def printScoreCard() = {
    val currentPlayer = controller.getCurrentPlayer
    val scoreCard = currentPlayer.scoreCard.categories.map { case (category, score) =>
      s"$category: ${score.getOrElse("_")}"
    }.mkString("\n")
    s"\nCurrent Player: ${currentPlayer.name}\nScoreCard:\n$scoreCard\n"
  }
}
