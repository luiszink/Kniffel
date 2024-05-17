package de.htwg.se.kniffel.controller

// State.scala
package de.htwg.se.kniffel.controller

trait State {
  def handleInput(input: String, controller: Controller): Unit
}

class RollingState extends State {
  override def handleInput(input: String, controller: Controller): Unit = {
    if (input.toLowerCase == "f") {
      controller.updateScore()
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
