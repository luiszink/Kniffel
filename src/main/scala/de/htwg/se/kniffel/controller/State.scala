
package de.htwg.se.kniffel.controller

trait State {
  def handleInput(input: String, controller: Controller): Unit
}

class RollingState extends State {
  override def handleInput(input: String, controller: Controller): Unit = {
    val scoreCardCategories = controller.getCurrentPlayer.scoreCard.categories

    if (scoreCardCategories.contains(input.toLowerCase)) {
      controller.updateScore(input.toLowerCase)
      controller.nextPlayer()
    } else {
      val diceToKeep = input.split(" ").map(_.toInt).toList
      controller.keepDice(diceToKeep)
    }
  }
}

class ScoringState extends State {
  override def handleInput(input: String, controller: Controller): Unit = {
    controller.updateScore(input)
    controller.nextPlayer()
  }
}
