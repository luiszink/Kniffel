package de.htwg.se.kniffel.model.modelImpl

import com.google.inject.Inject
import de.htwg.se.kniffel.model.{PlayerInterface, ScoreCardInterface, DiceInterface}
import scala.compiletime.uninitialized

case class Player @Inject() (name: String, scoreCard: ScoreCardInterface = ScoreCard()) extends PlayerInterface {
  private var dice: DiceInterface = uninitialized

  override def toString: String = name

  def getTotalScore: Int = {
    scoreCard.calculateTotalScore()
    scoreCard.categories("totalScore").getOrElse(0)
  }

  def setDice(newDice: DiceInterface): Unit = {
    dice = newDice
  }
}
