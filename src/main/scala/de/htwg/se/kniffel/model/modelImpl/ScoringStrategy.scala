package de.htwg.se.kniffel.model.modelImpl

import de.htwg.se.kniffel.model.ScoringStrategyInterface

object Ones extends ScoringStrategyInterface {
  override def calculateScore(dice: List[Int]): Int = dice.count(_ == 1) * 1
}

object Twos extends ScoringStrategyInterface {
  override def calculateScore(dice: List[Int]): Int = dice.count(_ == 2) * 2
}

object Threes extends ScoringStrategyInterface {
  override def calculateScore(dice: List[Int]): Int = dice.count(_ == 3) * 3
}

object Fours extends ScoringStrategyInterface {
  override def calculateScore(dice: List[Int]): Int = dice.count(_ == 4) * 4
}

object Fives extends ScoringStrategyInterface {
  override def calculateScore(dice: List[Int]): Int = dice.count(_ == 5) * 5
}

object Sixes extends ScoringStrategyInterface {
  override def calculateScore(dice: List[Int]): Int = dice.count(_ == 6) * 6
}

object ThreeTimes extends ScoringStrategyInterface {
  override def calculateScore(dice: List[Int]): Int = {
    if (dice.exists(d => dice.count(_ == d) >= 3)) dice.sum
    else 0
  }
}

object FourTimes extends ScoringStrategyInterface {
  override def calculateScore(dice: List[Int]): Int = {
    if (dice.exists(d => dice.count(_ == d) >= 4)) dice.sum
    else 0
  }
}

object FullHouse extends ScoringStrategyInterface {
  override def calculateScore(dice: List[Int]): Int = {
    val groupedDice = dice.groupBy(identity).mapValues(_.size)
    if (groupedDice.values.exists(_ == 3) && groupedDice.values.exists(_ == 2)) 25
    else 0
  }
}

object SmallStraight extends ScoringStrategyInterface {
  override def calculateScore(dice: List[Int]): Int = {
    val sortedDice = dice.distinct.sorted
    if (sortedDice.sliding(4).exists(d => d == List(d.head, d.head + 1, d.head + 2, d.head + 3))) 30
    else 0
  }
}

object LargeStraight extends ScoringStrategyInterface {
  override def calculateScore(dice: List[Int]): Int = {
    val sortedDice = dice.distinct.sorted
    if (sortedDice == List(1, 2, 3, 4, 5) || sortedDice == List(2, 3, 4, 5, 6)) 40
    else 0
  }
}

object Chance extends ScoringStrategyInterface {
  override def calculateScore(dice: List[Int]): Int = dice.sum
}

object Kniffel extends ScoringStrategyInterface {
  override def calculateScore(dice: List[Int]): Int = {
    if (dice.exists(d => dice.count(_ == d) == 5)) 50
    else 0
  }
}

object ScoreCalculator {
  def calculateScore(dice: List[Int], strategy: ScoringStrategyInterface): Int = {
    strategy.calculateScore(dice)
  }
}