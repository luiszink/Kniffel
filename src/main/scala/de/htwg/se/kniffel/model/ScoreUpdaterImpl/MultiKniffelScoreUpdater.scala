package de.htwg.se.kniffel.model.scoreUpdaterImpl

import scala.util.Random
import com.google.inject.Inject
import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.model.modelImpl._ 

class MultiKniffelScoreUpdater @Inject() extends ScoreUpdaterInterface {
  override def updateScore(player: PlayerInterface, category: String, dice: List[Int]): Unit = {
    // Logic for handling multiple Kniffel entries
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

      case Some(Some(_)) if category.toLowerCase == "kniffel" =>
        // Wenn die Kategorie "kniffel" bereits gefüllt ist, die 50 Punkte zufällig in eine andere leere Kategorie schreiben
        val emptyCategories = currentPlayer.scoreCard.categories.filter(_._2.isEmpty).keys.toList
        val emptyCategoriesExcluding = emptyCategories.filterNot(cat => cat == "bonus" || cat == "uppersectionscore" || cat == "totalscore")
        val randomCategory = emptyCategoriesExcluding(Random.nextInt(emptyCategoriesExcluding.size))
        currentPlayer.scoreCard.categories.update(randomCategory, Some(50))

      case _ => throw new IllegalArgumentException("Category already filled!")
    }
  }
}