package de.htwg.se.kniffel

import com.google.inject.AbstractModule
import de.htwg.se.kniffel.controller._
import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.util._
import com.google.inject.name.Names

class KniffelModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ControllerInterface]).to(classOf[Controller])
    bind(classOf[StateInterface]).annotatedWith(Names.named("RollingState")).to(classOf[RollingState])
    bind(classOf[StateInterface]).annotatedWith(Names.named("UpdateState")).to(classOf[UpdateState])
    bind(classOf[DiceInterface]).to(classOf[Dice])
    bind(classOf[Dice]).toProvider(classOf[DiceProvider])
    bind(classOf[PlayerInterface]).to(classOf[Player])
    bind(classOf[Player]).toInstance(new Player("PlayerName")) // Beispiel: Einen Standard-Spieler instanziieren
    bind(classOf[ScoringStrategy]).annotatedWith(Names.named("Ones")).toInstance(Ones)
    bind(classOf[ScoringStrategy]).annotatedWith(Names.named("Twos")).toInstance(Twos)
    bind(classOf[ScoringStrategy]).annotatedWith(Names.named("Threes")).toInstance(Threes)
    bind(classOf[ScoringStrategy]).annotatedWith(Names.named("Fours")).toInstance(Fours)
    bind(classOf[ScoringStrategy]).annotatedWith(Names.named("Fives")).toInstance(Fives)
    bind(classOf[ScoringStrategy]).annotatedWith(Names.named("Sixes")).toInstance(Sixes)
    bind(classOf[ScoringStrategy]).annotatedWith(Names.named("ThreeTimes")).toInstance(ThreeTimes)
    bind(classOf[ScoringStrategy]).annotatedWith(Names.named("FourTimes")).toInstance(FourTimes)
    bind(classOf[ScoringStrategy]).annotatedWith(Names.named("FullHouse")).toInstance(FullHouse)
    bind(classOf[ScoringStrategy]).annotatedWith(Names.named("SmallStraight")).toInstance(SmallStraight)
    bind(classOf[ScoringStrategy]).annotatedWith(Names.named("LargeStraight")).toInstance(LargeStraight)
    bind(classOf[ScoringStrategy]).annotatedWith(Names.named("Chance")).toInstance(Chance)
    bind(classOf[ScoringStrategy]).annotatedWith(Names.named("Kniffel")).toInstance(Kniffel)
    bind(classOf[ScoreCardInterface]).to(classOf[ScoreCard])
    bind(classOf[ScoreUpdater]).annotatedWith(Names.named("MultiKniffel")).to(classOf[MultiKniffelScoreUpdater])
    bind(classOf[ScoreUpdater]).annotatedWith(Names.named("Standard")).to(classOf[StandardScoreUpdater])
    bind(classOf[UndoManagerInterface]).to(classOf[UndoManager])
  }
}
