package de.htwg.se.kniffel.model.scoreUpdaterImpl

import scala.util.Random
import com.google.inject.Inject
import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.model.modelImpl._ 


class StandardScoreUpdater @Inject() extends ScoreUpdaterInterface {
  override def updateScore(player: PlayerInterface, category: String, dice: List[Int]): Unit = {
    val currentPlayer = player
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
    val calculatedScore = ScoreCalculator.calculateScore(dice, strategy)
    currentPlayer.scoreCard.categories.get(category.toLowerCase) match {
      case Some(None) =>
        currentPlayer.scoreCard.categories.update(category.toLowerCase, Some(calculatedScore))
      case _ => throw new IllegalArgumentException("Category already filled!")
    }
  }
}