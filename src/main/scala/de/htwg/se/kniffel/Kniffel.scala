package de.htwg.se.kniffel.aview

import scala.io.StdIn
import scala.util.Random
import de.htwg.se.kniffel.model.Dice
import de.htwg.se.kniffel.controller.Controller
import scala.concurrent.Future
import scala.concurrent.Await

object KniffelApp {

  def main(args: Array[String]): Unit = {
    val controller: Controller = new Controller()
    val tui: TUI = new TUI(controller)
    val gui: GUI = new GUI(controller);

    implicit val context = scala.concurrent.ExecutionContext.global
    val f = Future {
      gui.main(Array[String]())
    }
    tui.run()
    Await.ready(f, scala.concurrent.duration.Duration.Inf)
    // Test
  }
}

