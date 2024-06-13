package de.htwg.se.kniffel.model

object ScoreUpdaterFactory {
  def createScoreUpdater(userInput: String): ScoreUpdater = {
    userInput.toLowerCase match {
      case "y" => new MultiKniffelScoreUpdater()
      case _ => new StandardScoreUpdater()
    }
  }
}