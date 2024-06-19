package de.htwg.se.kniffel.model

import com.google.inject.Inject

case class Player @Inject() (name: String, scoreCard: ScoreCardInterface = ScoreCard()) extends PlayerInterface {
  override def toString: String = name
}
