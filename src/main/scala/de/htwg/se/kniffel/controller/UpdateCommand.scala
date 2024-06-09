// UpdateScoreCommand.scala
package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.model.Player
import de.htwg.se.kniffel.model.ScoreUpdaterFactory
import de.htwg.se.kniffel.model.StandardScoreUpdater
import de.htwg.se.kniffel.model.Dice
import de.htwg.se.kniffel.model.ScoringStrategy
import de.htwg.se.kniffel.model.ScoreCalculator
import de.htwg.se.kniffel.model.Ones
import de.htwg.se.kniffel.model.Twos
import de.htwg.se.kniffel.model.Threes
import de.htwg.se.kniffel.model.Fours
import de.htwg.se.kniffel.model.Fives
import de.htwg.se.kniffel.model.Sixes
import de.htwg.se.kniffel.model.ThreeTimes
import de.htwg.se.kniffel.model.FourTimes
import de.htwg.se.kniffel.model.FullHouse
import de.htwg.se.kniffel.model.SmallStraight
import de.htwg.se.kniffel.model.LargeStraight
import de.htwg.se.kniffel.model.Chance
import de.htwg.se.kniffel.model.Kniffel
import de.htwg.se.kniffel.util.Observable
import de.htwg.se.kniffel.util.UndoManager
import de.htwg.se.kniffel.util.KniffelEvent
import de.htwg.se.kniffel.util.Command

class UpdateScoreCommand(player: Player, category: String, dice: List[Int]) extends Command {

  private var previousScore: Option[Int] = player.scoreCard.categories.getOrElse(category.toLowerCase, None)
  private var newScore: Option[Int] = None

  override def doStep: Unit = {
    val strategy: ScoringStrategy = category.toLowerCase match {
      case "one" => Ones
      case "two" => Twos
      case "three" => Threes
      case "four" => Fours
      case "five" => Fives
      case "six" => Sixes
      case "threeofakind" => ThreeTimes
      case "fourofakind" => FourTimes
      case "fullhouse" => FullHouse
      case "smallstraight" => SmallStraight
      case "largestraight" => LargeStraight
      case "chance" => Chance
      case "kniffel" => Kniffel
      case _ => throw new IllegalArgumentException("Invalid category.")
    }

    newScore = Some(ScoreCalculator.calculateScore(dice, strategy))
    player.scoreCard.categories.update(category.toLowerCase, newScore)
  }

  override def undoStep: Unit = {
    player.scoreCard.categories.update(category.toLowerCase, previousScore)
  }

  override def redoStep: Unit = {
    player.scoreCard.categories.update(category.toLowerCase, newScore)
  }
}
