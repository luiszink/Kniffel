package de.htwg.se.kniffel.model

object ScoreUpdaterFactory {
  def createScoreUpdater(userInput: String): ScoreUpdater = {
    userInput.toLowerCase match {
      case "n" => new StandardScoreUpdater()
      case "y" => new MultiKniffelScoreUpdater()
      case _ => throw new IllegalArgumentException("Invalid input for ScoreUpdater type.") //try implementieren
    }
  }
}