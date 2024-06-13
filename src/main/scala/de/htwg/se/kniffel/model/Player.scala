package de.htwg.se.kniffel.model

case class Player(name: String, scoreCard: ScoreCardInterface = ScoreCard()) extends PlayerInterface {
  override def toString: String = name
}