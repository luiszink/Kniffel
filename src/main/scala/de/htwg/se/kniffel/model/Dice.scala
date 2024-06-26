package de.htwg.se.kniffel.model

import scala.util.Random

case class Dice(values: List[Int]) extends DiceInterface {

  // Auxiliary constructor to initialize Dice with random values
  def this() = {
    this(List.fill(5)(Dice.rollDice())) // Use the companion object's method
  }

  def keepDice(keepIndices: List[Int]): DiceInterface = {
    val (keptDice, _) = values.zipWithIndex.partition { case (_, index) =>
      keepIndices.contains(index + 1)}
    val newDice = values.diff(keptDice.map(_._1))
    val rerolledDice = List.fill(newDice.length)(Dice.rollDice())
    Dice(keptDice.map(_._1) ++ rerolledDice)
  }
}

object Dice {
  // Method to roll a single die
  def rollDice(): Int = Random.nextInt(6) + 1
}
