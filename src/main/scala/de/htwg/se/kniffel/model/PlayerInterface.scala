package de.htwg.se.kniffel.model

trait PlayerInterface {
  def name: String
  def scoreCard: ScoreCardInterface
  def getTotalScore: Int 
}
