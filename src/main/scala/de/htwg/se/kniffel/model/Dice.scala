package de.htwg.se.kniffel.model

import scala.util.Random

case class Dice(values: List[Int]) {

  // Auxiliary constructor to initialize Dice with random values
  def this() = {
    this(List.fill(5)(Dice.rollDice())) // Use the companion object's method
  }

  // Method to reroll the dice, keeping some and rolling new ones for others
  def keepDice(keepIndices: List[Int]): Dice = {
    val (keptDice, _) = values.zipWithIndex.partition { case (_, index) =>
      keepIndices.contains(index)
    }
    val newDiceCount = values.length - keptDice.map(_._1).length
    val rerolledDice = List.fill(newDiceCount)(Dice.rollDice()) // Use the companion object's method
    Dice(keptDice.map(_._1) ++ rerolledDice)
  }
}

object Dice {
  // Method to roll a single die
  def rollDice(): Int = Random.nextInt(6) + 1
}
