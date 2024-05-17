package de.htwg.se.kniffel.aview

import de.htwg.se.kniffel.controller.Controller

trait State {
  def handleInput(tui: TUI): Unit
}