package de.htwg.se.kniffel

import scala.io.StdIn
import scala.util.Random
import de.htwg.se.kniffel.controller.ControllerInterface
import de.htwg.se.kniffel.aview.{GUI, TUI}
import de.htwg.se.kniffel.model.ScoringStrategyInterface
import com.google.inject.{Guice, Injector, AbstractModule}
import de.htwg.se.kniffel.model.fileIoComponents._
import de.htwg.se.kniffel.model.fileIoComponents.fileIoJsonImpl.FileIoJsonImpl
import de.htwg.se.kniffel.model.fileIoComponents.fileIoXmlImpl.FileIoXmlImpl
import de.htwg.se.kniffel.model.modelImpl.{Player, ScoreCard}
import de.htwg.se.kniffel.model.PlayerInterface
import scala.concurrent.{Future, Await, ExecutionContext}
import scala.concurrent.duration.Duration

import com.google.inject.{Guice, Injector, AbstractModule}

object KniffelApp {
  def main(args: Array[String]): Unit = {
    val injector: Injector = Guice.createInjector(new KniffelModule)

    val tui: TUI = injector.getInstance(classOf[TUI])
    val gui: GUI = injector.getInstance(classOf[GUI])
    val controller: ControllerInterface = injector.getInstance(classOf[ControllerInterface])

    implicit val context: ExecutionContext = scala.concurrent.ExecutionContext.global
    val f = Future {
      gui.main(Array[String]())
    }

    tui.run()
    Await.ready(f, Duration.Inf)
  }
}
