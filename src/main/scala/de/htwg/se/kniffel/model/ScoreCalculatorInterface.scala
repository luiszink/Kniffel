package de.htwg.se.kniffel.model

trait ScoringStrategy {
  def calculateScore(dice: List[Int]): Int
}