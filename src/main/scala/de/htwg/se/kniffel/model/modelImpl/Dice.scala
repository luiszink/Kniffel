package de.htwg.se.kniffel.model.modelImpl

import scala.util.Random
import com.google.inject.{Inject, Provider}
import de.htwg.se.kniffel.model.DiceInterface

class Dice @Inject() (val values: List[Int]) extends DiceInterface {
  def keepDice(keepIndices: List[Int]): DiceInterface = {
    val newValues = values.zipWithIndex.map { case (value, index) =>
      if (keepIndices.contains(index + 1)) value else Dice.rollDice()
    }
    new Dice(newValues)
  }
}

object Dice {
  def rollDice(): Int = Random.nextInt(6) + 1
}

class DiceProvider @Inject extends Provider[Dice] {
  override def get(): Dice = new Dice(List.fill(5)(Dice.rollDice()))
}
