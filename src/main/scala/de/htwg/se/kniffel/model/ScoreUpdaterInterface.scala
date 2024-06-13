package de.htwg.se.kniffel.model

trait ScoreUpdater {
  def updateScore(player: PlayerInterface, category: String, dice: List[Int]): Unit
}
