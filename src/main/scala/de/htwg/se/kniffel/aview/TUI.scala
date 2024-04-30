package de.htwg.se.kniffel.aview

import scala.io.StdIn
import scala.util.Random
import de.htwg.se.kniffel.model.Dice
import de.htwg.se.kniffel.util.Observer
import de.htwg.se.kniffel.controller.Controller

class TUI(controller: Controller) extends Observer {

  // add to subscribers
  controller.add(this)

  override def update: Unit = {
     println(printGame())
  }

  def printGame() = {
    val diceValues: List[Int] = controller.getDice
    val horizontalLine =
      "+" + List.fill(diceValues.length)("---").mkString("+") + "+"
    val diceIconsLine =
      "|" + diceValues.map(value => s" $value ").mkString("|") + "|"
    val numCounter = " " + diceValues.indices
      .map(index => s" ${index + 1} ")
      .mkString(" ") + " "

    s"$horizontalLine\n$diceIconsLine\n$horizontalLine\n$numCounter"
  }

  def input(): Option[List[Int]] = {
    // Start the game and display the final dice values
    println(
      s"Enter the indices of the dice you want to keep (e.g., 1 3 5), or type 'f' to end (${controller.repetitions} remaining):"
    )
    val input = StdIn.readLine()

    if (input.toLowerCase == "f") {
      println("Ending the game...")
      None
    } else {
      Option(input.split(" ").map(_.toInt).toList)
    }
  }
  var running = true
  
  def run() = {
    println(printGame())
    while (running) {
      input() match {
        case Some(value) => {
          controller.keepDice(value)
        }
        case None => running = false
      }
    }
  }
}
