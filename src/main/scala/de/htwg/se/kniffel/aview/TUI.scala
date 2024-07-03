package de.htwg.se.kniffel.aview

import scala.io.StdIn
import de.htwg.se.kniffel.controller.ControllerInterface
import de.htwg.se.kniffel.util.{Observer, KniffelEvent}
import scala.util.{Try, Success, Failure}
import com.google.inject.Inject

class TUI @Inject() (controller: ControllerInterface) extends Observer {

  controller.add(this)

  var syncFlag = true

  override def update(event: KniffelEvent.Value): Unit = {
    event match {
      case KniffelEvent.keepDice       => 
        println(printDice())
        println(printRoll())
      case KniffelEvent.noRepetitions  => 
        println(printDice())
      case KniffelEvent.NextPlayer     => 
        println(printScoreCard())
      case KniffelEvent.updateScore     => 
        println(printDice())
        println(printRoll())
      case KniffelEvent.Undo           => 
        println(printDiceUndo())
        println(printScoreCard())
      case KniffelEvent.setScoreUpdater=>
        println(printDice())
        println(printRoll())
        val syncFlag = false
      case KniffelEvent.InvalidInput   =>
        println("Invalid input! Please try again.")
      case _ =>
    }
  }

  def run() = {
    println("Enter player names (comma-separated):")
    val input = handleInput(getInput())
    var running = true
    while (running) {
      controller.getCurrentState.name match {
        case "UpdateState" =>
          printScoreCard()
          println("Enter category (e.g., One, Fullhouse...!):")
          printDice()
          val input = handleInput(getInput())
          controller.handleInput(input)
        case _ =>
          syncFlag match
            case true =>
              val input = handleInput(getInput())
              controller.handleInput(input)
              syncFlag = true
            case false =>
              controller.handleInput(input)
              syncFlag = true
      }
    }
  }

  def getInput(): String = {
    val input = StdIn.readLine()
    input
  }

  def handleInput(input: String): String = {
    if (controller.getPlayers.isEmpty) {
      addPlayers(input)
      println("Do you want to allow multiple Kniffel? (y/n)")
      selectScoreUpdater(getInput())
      syncFlag = false
      handleInput(getInput())
    } else {
      input
    }
  }

  def addPlayers(input: String): Unit = {
    Try {
      val names = input.split(",").map(_.trim)
      if (names.isEmpty || names.exists(_.isEmpty)) {
        throw new IllegalArgumentException(
          "Invalid input! Please enter at least one name, separated by commas."
        )
      }
      names.foreach(controller.addPlayer)
    } match {
      case Success(_) =>
      case Failure(_) =>
        println("Invalid input! Please try again.")
        addPlayers(input)
    }
  }

  def selectScoreUpdater(input: String): Unit = {
    Try {
      controller.setScoreUpdater(input)
    } match {
      case Success(_) =>
      case Failure(_) =>
        println("Invalid input! Please try again.")
        selectScoreUpdater(input)
    }
  }

  def printDiceUndo() = {
    val diceValues: List[Int] = controller.getPreviousDice
    val horizontalLine =
      "+" + List.fill(diceValues.length)("---").mkString("+") + "+"
    val diceIconsLine =
      "|" + diceValues.map(value => s" $value ").mkString("|") + "|"
    val numCounter = " " + diceValues.indices
      .map(index => s" ${index + 1} ")
      .mkString(" ") + " "
    s"Current Player: ${controller.getCurrentPlayer}\n$horizontalLine\n$diceIconsLine\n$horizontalLine\n$numCounter\n"
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
    s"Current Player: ${controller.getCurrentPlayer}\n$horizontalLine\n$diceIconsLine\n$horizontalLine\n$numCounter\n"
  }

  def printScoreCard() = {
    val currentPlayer = controller.getCurrentPlayer
    val scoreCard = currentPlayer.scoreCard.categories
      .map { case (category, score) =>
        s"$category: ${score.getOrElse("_")}"
      }
      .mkString("\n")
    s"\nCurrent Player: ${currentPlayer.name}\nScoreCard:\n$scoreCard\n"
  }

  def printRoll() = {
    println(s"Enter the indices of the dice you want to keep (e.g., 1 3 5), or Enter category (e.g., One, Fullhouse...) (${controller.repetitions} remaining), or 'undo' to undo last score update:")
  }
}
