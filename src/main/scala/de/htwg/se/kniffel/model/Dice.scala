package de.htwg.se.kniffel.model

import scala.util.Random
import de.htwg.se.kniffel.model.Dice.rollDice

case class Dice(values: List[Int]) {

  // Auxiliary constructor to initialize Dice with random values
  def this() = {
    this(List.fill(5)(Dice.rollDice())) // Use the companion object's method
  }

  def keepDice(keepIndices: List[Int]): Dice = {
    val (keptDice, _) = values.zipWithIndex.partition { case (_, index) =>
      keepIndices.contains(index + 1)}
    val newDice = values.diff(keptDice.map(_._1))
    val rerolledDice = List.fill(newDice.length)(rollDice())
    Dice(keptDice.map(_._1) ++ rerolledDice)
  }
}

object Dice {
  // Method to roll a single die
  def rollDice(): Int = Random.nextInt(6) + 1
}