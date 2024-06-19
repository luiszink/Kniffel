package de.htwg.se.kniffel

import scala.io.StdIn
import scala.util.Random
import de.htwg.se.kniffel.controller.{Controller, ControllerInterface}
import de.htwg.se.kniffel.aview.{GUI, TUI}
import de.htwg.se.kniffel.model.ScoringStrategy
import com.google.inject.{Guice, Inject, Injector}
import com.google.inject.name.Named
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration

object KniffelApp {
  def main(args: Array[String]): Unit = {
    val injector: Injector = Guice.createInjector(new KniffelModule)
    val tui: TUI = injector.getInstance(classOf[TUI])
    val gui: GUI = injector.getInstance(classOf[GUI])

    implicit val context = scala.concurrent.ExecutionContext.global
    val f = Future {
      gui.main(Array[String]())
    }
    tui.run()
    Await.ready(f, Duration.Inf)
  }
}
