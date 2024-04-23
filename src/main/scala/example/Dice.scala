package example

import scala.util.Random
import example.Dice.rollDice

case class Dice(val values: List[Int]) {

  def this() {
    this(List.fill(5)(rollDice()))
  }
  // Recursive method to reroll the dice with a specified number of repetitions
  def keepDice(keepIndices: List[Int]): Dice = {
    val (keptDice, _) = values.zipWithIndex.partition { case (_, index) =>
      keepIndices.contains(index + 1)
    }
    val newDice = values.diff(keptDice.map(_._1))
    val rerolledDice = List.fill(newDice.length)(rollDice())
    Dice(keptDice.map(_._1) ++ rerolledDice)
  }
}

object Dice {
  // Method to roll a single dice and return its icon as a string
  def rollDice(): Int = {
    Random.nextInt(6) + 1
  }
}
