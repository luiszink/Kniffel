package de.htwg.se.kniffel.model

class MultiKniffelScoreUpdater extends ScoreUpdater {
  override def updateScore(player: PlayerInterface, category: String, dice: List[Int]): Unit = {
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
      case _ => throw new IllegalArgumentException("Invalid category.")
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
