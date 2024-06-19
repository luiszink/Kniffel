import com.google.inject.AbstractModule
import de.htwg.se.kniffel.aview.{GUI, TUI}
import de.htwg.se.kniffel.controller._
import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.util._

class KniffelModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ControllerInterface]).to(classOf[Controller])
    bind(classOf[StateInterface]).to(classOf[RollingState])
    bind(classOf[StateInterface]).to(classOf[UpdateState])
    bind(classOf[DiceInterface]).to(classOf[Dice])
    bind(classOf[PlayerInterface]).to(classOf[Player])
    bind(classOf[ScoreCalculatorInterface]).to(classOf[ScoreCalculator])
    bind(classOf[ScoreCardInterface]).to(classOf[ScoreCard])
    bind(classOf[ScoreUpdaterInterface]).to(classOf[ScoreUpdaterImpl])
    bind(classOf[UndoManagerInterface]).to(classOf[UndoManager])
    // Weitere Bindungen nach Bedarf
  }
}
