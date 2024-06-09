
package de.htwg.se.kniffel.controller
// State Pattern

trait State {
  def name: String
  def handleInput(input: String, controller: Controller): Unit
}

class RollingState extends State {
  override def name: String = "RollingState"
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

class UpdateState extends State {
  override def name: String = "updateState"
  override def handleInput(input: String, controller: Controller): Unit = {
    controller.updateScore(input.toLowerCase)
    controller.nextPlayer()
  }
}
