package de.htwg.se.kniffel.model.modelImpl

import com.google.inject.Inject
import de.htwg.se.kniffel.model.{PlayerInterface, ScoreCardInterface}

case class Player @Inject() (name: String, scoreCard: ScoreCardInterface = ScoreCard()) extends PlayerInterface {
  override def toString: String = name
}
