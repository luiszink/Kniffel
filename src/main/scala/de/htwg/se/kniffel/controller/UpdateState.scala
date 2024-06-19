package de.htwg.se.kniffel.controller

class UpdateState extends StateInterface {
  override def name: String = "UpdateState"
  override def handleInput(input: String, controller: Controller): Unit = {
    controller.updateScore(input.toLowerCase)
  }
}