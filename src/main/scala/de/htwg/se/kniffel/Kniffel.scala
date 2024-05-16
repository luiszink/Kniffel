package de.htwg.se.kniffel.aview

import scala.io.StdIn
import scala.util.Random
import de.htwg.se.kniffel.model.Dice
import de.htwg.se.kniffel.controller.Controller

object KniffelApp {

  def main(args: Array[String]): Unit = {
    val controller: Controller = new Controller()
    val tui: TUI = new TUI(controller)
    tui.run()
    // Test
  }
}
