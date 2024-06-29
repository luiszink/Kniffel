package de.htwg.se.kniffel.controller

trait StateInterface {
  def name: String
  def handleInput(input: String, controller: ControllerInterface): Unit
}