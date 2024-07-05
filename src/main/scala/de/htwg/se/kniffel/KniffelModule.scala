package de.htwg.se.kniffel

import com.google.inject.{AbstractModule, Provider}
import com.google.inject.name.Names
import com.google.inject.TypeLiteral
import de.htwg.se.kniffel.controller._
import de.htwg.se.kniffel.controller.controllerImpl.Controller
import de.htwg.se.kniffel.controller.controllerImpl.{RollingState, UpdateState}
import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.util._
import de.htwg.se.kniffel.model.modelImpl._
import de.htwg.se.kniffel.model.scoreUpdaterImpl._
import de.htwg.se.kniffel.aview.{GUI, TUI}
import scala.collection.immutable.List
import de.htwg.se.kniffel.model.fileIoComponents.fileIoXmlImpl.FileIoXmlImpl
import de.htwg.se.kniffel.model.fileIoComponents.fileIoJsonImpl.FileIoJsonImpl
import de.htwg.se.kniffel.model.fileIoComponents.FileIoInterface

class KniffelModule extends AbstractModule {
  override def configure(): Unit = {
    val jsonProvider = new Provider[FileIoJsonImpl] {override def get(): FileIoJsonImpl = new FileIoJsonImpl}
    val xmlProvider = new Provider[FileIoXmlImpl] {override def get(): FileIoXmlImpl = new FileIoXmlImpl}
    bind(classOf[ControllerInterface]).toInstance(new Controller(jsonProvider, xmlProvider))
    bind(classOf[StateInterface]).annotatedWith(Names.named("RollingState")).to(classOf[RollingState])
    bind(classOf[StateInterface]).annotatedWith(Names.named("UpdateState")).to(classOf[UpdateState])
    bind(classOf[DiceInterface]).to(classOf[Dice])
    bind(classOf[PlayerInterface]).to(classOf[Player])
    bind(classOf[ScoreCardInterface]).to(classOf[ScoreCard])
    bind(classOf[ScoreUpdaterInterface]).annotatedWith(Names.named("MultiKniffel")).to(classOf[MultiKniffelScoreUpdater])
    bind(classOf[ScoreUpdaterInterface]).annotatedWith(Names.named("Standard")).to(classOf[StandardScoreUpdater])
    bind(classOf[UndoManagerInterface]).to(classOf[UndoManager])
    bind(new TypeLiteral[List[Int]]() {}).toInstance(List.empty[Int])
    bind(classOf[FileIoInterface]).annotatedWith(Names.named("json")).to(classOf[FileIoJsonImpl])
    bind(classOf[FileIoInterface]).annotatedWith(Names.named("xml")).to(classOf[FileIoXmlImpl])
  }
}
