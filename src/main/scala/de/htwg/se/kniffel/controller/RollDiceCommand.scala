package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.util.Command
import de.htwg.se.kniffel.model.Dice

class RollDiceCommand(controller: Controller, previousDice: Dice) extends Command {
  private val newDice: Dice = new Dice(controller.getDice)

  override def doStep(): Unit = {
    controller.setDice(newDice)
  }

  override def undoStep(): Unit = {
    controller.setDice(previousDice)
  }

  override def redoStep(): Unit = {
    controller.setDice(newDice)
  }
}
