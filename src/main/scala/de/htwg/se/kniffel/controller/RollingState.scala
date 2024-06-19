package de.htwg.se.kniffel.controller

class RollingState extends StateInterface {
  override def name: String = "RollingState"
  override def handleInput(input: String, controller: Controller): Unit = {
    val scoreCardCategories = controller.getCurrentPlayer.scoreCard.categories

    if (scoreCardCategories.contains(input.toLowerCase)) {
      controller.updateScore(input.toLowerCase)
    } else {
      val diceToKeep = input.split(" ").map(_.toInt).toList
      controller.keepDice(diceToKeep)
    }
  }
}