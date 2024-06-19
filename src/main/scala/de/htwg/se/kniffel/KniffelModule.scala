package de.htwg.se.kniffel

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import de.htwg.se.kniffel.controller._
import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.util._
import de.htwg.se.kniffel.aview.{GUI, TUI}

class KniffelModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[StateInterface]).annotatedWith(Names.named("RollingState")).to(classOf[RollingState])
    bind(classOf[StateInterface]).annotatedWith(Names.named("UpdateState")).to(classOf[UpdateState])
    bind(classOf[DiceInterface]).to(classOf[Dice])
    bind(classOf[Dice]).toProvider(classOf[DiceProvider])
    bind(classOf[PlayerInterface]).to(classOf[Player])
    bind(classOf[ScoreCardInterface]).to(classOf[ScoreCard])
    bind(classOf[ScoreUpdater]).annotatedWith(Names.named("MultiKniffel")).to(classOf[MultiKniffelScoreUpdater])
    bind(classOf[ScoreUpdater]).annotatedWith(Names.named("Standard")).to(classOf[StandardScoreUpdater])
    bind(classOf[UndoManagerInterface]).to(classOf[UndoManager])
    bind(classOf[GUI]).toInstance(new GUI(new Controller)) // Beispiel-Injektion
    bind(classOf[TUI]).toInstance(new TUI(new Controller)) // Beispiel-Injektion
  }
}
