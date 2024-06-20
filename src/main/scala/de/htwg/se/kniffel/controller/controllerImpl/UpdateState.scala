package de.htwg.se.kniffel.controller.controllerImpl

import com.google.inject.Inject
import de.htwg.se.kniffel.controller.{StateInterface, ControllerInterface}

class UpdateState @Inject() extends StateInterface {
  override def name: String = "UpdateState"
  override def handleInput(input: String, controller: ControllerInterface): Unit = {
    controller.updateScore(input.toLowerCase)
  }
}
