package de.htwg.se.kniffel.controller.controllerImpl

import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.util._
import de.htwg.se.kniffel.model.modelImpl._

class UpdateScoreCommand(player: PlayerInterface, category: String, dice: List[Int]) extends Command {

  private var previousScore: Option[Int] = player.scoreCard.categories.getOrElse(category.toLowerCase, None)
  private var newScore: Option[Int] = None

  override def doStep: Unit = {
    val strategy: ScoringStrategyInterface = category.toLowerCase match {
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
