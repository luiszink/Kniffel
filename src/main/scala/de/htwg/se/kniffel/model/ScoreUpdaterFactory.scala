// ScoreUpdaterFactory.scala
package de.htwg.se.kniffel.model

object ScoreUpdaterFactory {
  def createScoreUpdater(userInput: String): ScoreUpdater = {
    userInput.toLowerCase match {
      case "y" => new StandardScoreUpdater()
      case "n" => new SpecialScoreUpdater()
      case _ => throw new IllegalArgumentException("Invalid input for ScoreUpdater type.")
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
      case _ => throw new IllegalArgumentException("Invalid category.")
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

class SpecialScoreUpdater extends ScoreUpdater {
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
      case _ => throw new IllegalArgumentException("Invalid category.")
    }
    val calculatedScore = ScoreCalculator.calculateScore(dice, strategy)
    currentPlayer.scoreCard.categories.get(category.toLowerCase) match {
      case Some(None) =>
        currentPlayer.scoreCard.categories.update(category.toLowerCase, Some(calculatedScore))
        println("123")
      case _ =>
        println(s"Category $category is already filled or does not exist.")
        println("123")
    }
  }
}
