// ScoreUpdaterFactory.scala
package de.htwg.se.kniffel.model

trait ScoreUpdater {
  def updateScore(player: Player, category: String, dice: List[Int]): Unit
}

object ScoreUpdaterFactory {
  def createScoreUpdater(userInput: String): ScoreUpdater = {
    userInput.toLowerCase match {
      case "y" => new MultiKniffelScoreUpdater()
      case "n" => new StandardScoreUpdater()
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
    currentPlayer.scoreCard.categories.get(category.toLowerCase) match {
      case Some(None) =>
        currentPlayer.scoreCard.categories.update(category.toLowerCase, Some(calculatedScore))
      case _ =>
        println(s"Category $category is already filled or does not exist.")
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
      case _ =>
        if (category.toLowerCase == "kniffel" && currentPlayer.scoreCard.categories("kniffel").isDefined) {
          // Allow multiple Kniffel entries
          val currentScore = currentPlayer.scoreCard.categories("kniffel").getOrElse(0)
          currentPlayer.scoreCard.categories.update("kniffel", Some(currentScore + calculatedScore))
        } else {
          println(s"Category $category is already filled or does not exist.")
        }
    }
  }
}