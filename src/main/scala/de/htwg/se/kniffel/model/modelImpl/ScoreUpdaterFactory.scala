package de.htwg.se.kniffel.model.modelImpl

import de.htwg.se.kniffel.model.scoreUpdaterImpl._
import de.htwg.se.kniffel.model.ScoreUpdaterInterface

object ScoreUpdaterFactory {
  def createScoreUpdater(userInput: String): ScoreUpdaterInterface = {
    userInput.toLowerCase match {
      case "n" => new StandardScoreUpdater()
      case "y" => new MultiKniffelScoreUpdater()
      case _ => throw new IllegalArgumentException("Invalid input for ScoreUpdater type.") //try implementieren
    }
  }
}