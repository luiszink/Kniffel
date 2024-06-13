package de.htwg.se.kniffel.model

trait DiceInterface {
  def values: List[Int]
  def keepDice(keepIndices: List[Int]): DiceInterface
}
