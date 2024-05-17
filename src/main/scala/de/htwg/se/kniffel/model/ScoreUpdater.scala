// ScoreUpdater.scala
package de.htwg.se.kniffel.model

trait ScoreUpdater {
  def updateScore(player: Player, category: String, dice: List[Int]): Unit
}