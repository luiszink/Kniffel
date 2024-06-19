package de.htwg.se.kniffel.controller

import com.google.inject.Inject

class UpdateState @Inject() extends StateInterface {
  override def name: String = "UpdateState"
  override def handleInput(input: String, controller: Controller): Unit = {
    controller.updateScore(input.toLowerCase)
  }
}