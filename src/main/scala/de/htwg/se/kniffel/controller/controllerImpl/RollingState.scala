package de.htwg.se.kniffel.controller.controllerImpl

import com.google.inject.Inject
import de.htwg.se.kniffel.controller.{StateInterface, ControllerInterface}

class RollingState @Inject() extends StateInterface {
  override def name: String = "RollingState"
  override def handleInput(input: String, controller: ControllerInterface): Unit = {
    val scoreCardCategories = controller.getCurrentPlayer.scoreCard.categories

    if (scoreCardCategories.contains(input.toLowerCase)) {
      controller.updateScore(input.toLowerCase)
    } else {
      val diceToKeep = input.split(" ").map(_.toInt).toList
      controller.keepDice(diceToKeep)
    }
  }
}
