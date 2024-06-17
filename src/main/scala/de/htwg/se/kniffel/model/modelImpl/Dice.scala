package de.htwg.se.kniffel.model.modelImpl

import scala.util.Random
import de.htwg.se.kniffel.model._

case class Dice(values: List[Int] = List.fill(5)(Dice.rollDice())) extends DiceInterface {

  def keepDice(keepIndices: List[Int]): DiceInterface = {
    val newValues = values.zipWithIndex.map { case (value, index) =>
      if (keepIndices.contains(index + 1)) value else Dice.rollDice()
    }
    Dice(newValues)
  }
}

object Dice {
  def rollDice(): Int = Random.nextInt(6) + 1
}