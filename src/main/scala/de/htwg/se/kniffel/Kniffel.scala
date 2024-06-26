import scala.io.StdIn
import scala.util.Random
import de.htwg.se.kniffel.model.Dice
import de.htwg.se.kniffel.controller.{Controller, ControllerInterface}
import scala.concurrent.{Future, Await}
import de.htwg.se.kniffel.aview.gui.GUI
import de.htwg.se.kniffel.aview.TUI

object KniffelApp {

  def main(args: Array[String]): Unit = {
    val controller: ControllerInterface = new Controller()
    val tui: TUI = new TUI(controller)
    val gui: GUI = new GUI(controller);

    implicit val context = scala.concurrent.ExecutionContext.global
    val f = Future {
      gui.main(Array[String]())
    }
    tui.run()
    Await.ready(f, scala.concurrent.duration.Duration.Inf)
  }
}

