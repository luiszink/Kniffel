package de.htwg.se.kniffel.model

trait ScoreUpdaterInterface {
  def updateScore(player: PlayerInterface, category: String, dice: List[Int]): Unit
}
