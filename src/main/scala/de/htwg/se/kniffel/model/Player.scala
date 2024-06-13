package de.htwg.se.kniffel.model

case class Player(name: String, scoreCard: ScoreCard = ScoreCard()) extends PlayerInterface {
  override def toString: String = name
}