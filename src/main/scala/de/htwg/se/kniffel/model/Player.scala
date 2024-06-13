package de.htwg.se.kniffel.model

import de.htwg.se.kniffel.model.ScoreCard

case class Player(name: String, scoreCard: ScoreCard = ScoreCard()) {
  override def toString: String = name
}
