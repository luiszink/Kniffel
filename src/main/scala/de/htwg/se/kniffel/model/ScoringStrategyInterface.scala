package de.htwg.se.kniffel.model

trait ScoringStrategyInterface {
  def calculateScore(dice: List[Int]): Int
}