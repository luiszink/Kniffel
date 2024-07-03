package de.htwg.se.kniffel.model.modelImpl

import de.htwg.se.kniffel.model.ScoreUpdaterInterface
import de.htwg.se.kniffel.model.scoreUpdaterImpl.{StandardScoreUpdater, MultiKniffelScoreUpdater}

object ScoreUpdaterFactory {
  def createScoreUpdater(userInput: String): ScoreUpdaterInterface = {
    userInput.toLowerCase match {
      case "y" => new MultiKniffelScoreUpdater()
      case "n" => new StandardScoreUpdater()
      case _ => throw new IllegalArgumentException("Invalid input for ScoreUpdater type.")
    }
  }
}
