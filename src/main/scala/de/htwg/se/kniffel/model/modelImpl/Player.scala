package de.htwg.se.kniffel.model.modelImpl

import de.htwg.se.kniffel.model._

case class Player(name: String, scoreCard: ScoreCardInterface = ScoreCard()) extends PlayerInterface {
  override def toString: String = name
}