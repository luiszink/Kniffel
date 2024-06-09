// ScoreUpdaterFactory.scala
package de.htwg.se.kniffel.model

import scala.util.Random

trait ScoreUpdater {
  def updateScore(player: Player, category: String, dice: List[Int]): Unit
}

object ScoreUpdaterFactory {
  def createScoreUpdater(userInput: String): ScoreUpdater = {
    userInput.toLowerCase match {
      case "n" => new StandardScoreUpdater()
      case "y" => new MultiKniffelScoreUpdater()
      case _ => throw new IllegalArgumentException("Invalid input for ScoreUpdater type.") //try implementieren
    }
  }
}

class StandardScoreUpdater extends ScoreUpdater {
  override def updateScore(player: Player, category: String, dice: List[Int]): Unit = {
    val currentPlayer = player
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
    }
    val calculatedScore = ScoreCalculator.calculateScore(dice, strategy)

    // it has to throw an error if the categorie is already filled
    currentPlayer.scoreCard.categories.get(category.toLowerCase) match {
      case Some(None) =>
        currentPlayer.scoreCard.categories.update(category.toLowerCase, Some(calculatedScore))
      case _ => throw new IllegalArgumentException("Category already filled!")
    }
  }
}

class MultiKniffelScoreUpdater extends ScoreUpdater {
  override def updateScore(player: Player, category: String, dice: List[Int]): Unit = {
    // Logic for handling multiple Kniffel entries
    val currentPlayer = player
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