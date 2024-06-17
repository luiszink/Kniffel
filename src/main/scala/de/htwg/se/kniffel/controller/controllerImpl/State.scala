package de.htwg.se.kniffel.controller.controllerImpl

import de.htwg.se.kniffel.controller.{ControllerInterface, StateInterface}

class RollingState extends StateInterface {
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

class UpdateState extends StateInterface {
  override def name: String = "UpdateState"
  override def handleInput(input: String, controller: ControllerInterface): Unit = {
    controller.updateScore(input.toLowerCase)
  }
}