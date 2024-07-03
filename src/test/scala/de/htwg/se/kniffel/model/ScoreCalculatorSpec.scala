package de.htwg.se.kniffel.model.modelImpl

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.kniffel.model._

class ScoringStrategyTest extends AnyFlatSpec with Matchers {

  "Ones" should "calculate the score for Ones category" in {
    Ones.calculateScore(List(1, 1, 2, 3, 1)) should be(3)
  }

  it should "return 0 if no ones are rolled" in {
    Ones.calculateScore(List(2, 3, 4, 5, 6)) should be(0)
  }

  it should "return 0 if no dice are rolled" in {
    Ones.calculateScore(List()) should be(0)
  }

  "Twos" should "calculate the score for Twos category" in {
    Twos.calculateScore(List(2, 2, 2, 3, 4)) should be(6)
  }

  it should "return 0 if no twos are rolled" in {
    Twos.calculateScore(List(1, 1, 3, 4, 5)) should be(0)
  }

  it should "return 0 if no dice are rolled" in {
    Twos.calculateScore(List()) should be(0)
  }

  "Threes" should "calculate the score for Threes category" in {
    Threes.calculateScore(List(3, 3, 3, 4, 5)) should be(9)
  }

  it should "return 0 if no threes are rolled" in {
    Threes.calculateScore(List(1, 2, 4, 5, 6)) should be(0)
  }

  it should "return 0 if no dice are rolled" in {
    Threes.calculateScore(List()) should be(0)
  }

  "Fours" should "calculate the score for Fours category" in {
    Fours.calculateScore(List(4, 4, 4, 4, 5)) should be(16)
  }

  it should "return 0 if no fours are rolled" in {
    Fours.calculateScore(List(1, 2, 3, 5, 6)) should be(0)
  }

  it should "return 0 if no dice are rolled" in {
    Fours.calculateScore(List()) should be(0)
  }

  "Fives" should "calculate the score for Fives category" in {
    Fives.calculateScore(List(5, 5, 5, 4, 1)) should be(15)
  }

  it should "return 0 if no fives are rolled" in {
    Fives.calculateScore(List(1, 2, 3, 4, 6)) should be(0)
  }

  it should "return 0 if no dice are rolled" in {
    Fives.calculateScore(List()) should be(0)
  }

  "Sixes" should "calculate the score for Sixes category" in {
    Sixes.calculateScore(List(6, 6, 6, 4, 1)) should be(18)
  }

  it should "return 0 if no sixes are rolled" in {
    Sixes.calculateScore(List(1, 2, 3, 4, 5)) should be(0)
  }

  it should "return 0 if no dice are rolled" in {
    Sixes.calculateScore(List()) should be(0)
  }

  "ThreeTimes" should "calculate the score for ThreeTimes category" in {
    ThreeTimes.calculateScore(List(1, 1, 1, 2, 2)) should be(7)
  }

  it should "return 0 if no three of a kind is rolled" in {
    ThreeTimes.calculateScore(List(1, 1, 2, 2, 3)) should be(0)
  }

  it should "return 0 if no dice are rolled" in {
    ThreeTimes.calculateScore(List()) should be(0)
  }

  "FourTimes" should "calculate the score for FourTimes category" in {
    FourTimes.calculateScore(List(1, 1, 1, 1, 2)) should be(6)
  }

  it should "return 0 if no four of a kind is rolled" in {
    FourTimes.calculateScore(List(1, 1, 1, 2, 2)) should be(0)
  }

  it should "return 0 if no dice are rolled" in {
    FourTimes.calculateScore(List()) should be(0)
  }

  "FullHouse" should "calculate the score for FullHouse category" in {
    FullHouse.calculateScore(List(1, 1, 2, 2, 2)) should be(25)
  }

  it should "return 0 if no full house is rolled" in {
    FullHouse.calculateScore(List(1, 2, 2, 3, 3)) should be(0)
  }

  it should "return 0 if no dice are rolled" in {
    FullHouse.calculateScore(List()) should be(0)
  }

  "SmallStraight" should "calculate the score for SmallStraight category" in {
    SmallStraight.calculateScore(List(1, 2, 3, 4, 6)) should be(30)
  }

  it should "return 0 if no small straight is rolled" in {
    SmallStraight.calculateScore(List(1, 2, 3, 5, 6)) should be(0)
  }

  it should "return 0 if no dice are rolled" in {
    SmallStraight.calculateScore(List()) should be(0)
  }

  "LargeStraight" should "calculate the score for LargeStraight category" in {
    LargeStraight.calculateScore(List(1, 2, 3, 4, 5)) should be(40)
  }

  it should "return 0 if no large straight is rolled" in {
    LargeStraight.calculateScore(List(1, 2, 3, 4, 6)) should be(0)
  }

  it should "return 0 if no dice are rolled" in {
    LargeStraight.calculateScore(List()) should be(0)
  }

  "Chance" should "calculate the score for Chance category" in {
    Chance.calculateScore(List(1, 2, 3, 4, 6)) should be(16)
  }

  it should "return 0 if no dice are rolled" in {
    Chance.calculateScore(List()) should be(0)
  }

  "Kniffel" should "calculate the score for Kniffel category" in {
    Kniffel.calculateScore(List(3, 3, 3, 3, 3)) should be(50)
  }

  it should "return 0 if no Kniffel (five of a kind) is rolled" in {
    Kniffel.calculateScore(List(1, 2, 3, 4, 5)) should be(0)
  }

  it should "return 0 if no dice are rolled" in {
    Kniffel.calculateScore(List()) should be(0)
  }

  "ScoreCalculator" should "calculate the score based on the given strategy" in {
    ScoreCalculator.calculateScore(List(1, 1, 2, 3, 1), Ones) should be(3)
    ScoreCalculator.calculateScore(List(2, 2, 2, 3, 4), Twos) should be(6)
    ScoreCalculator.calculateScore(List(3, 3, 3, 4, 5), Threes) should be(9)
    ScoreCalculator.calculateScore(List(4, 4, 4, 4, 5), Fours) should be(16)
    ScoreCalculator.calculateScore(List(5, 5, 5, 4, 1), Fives) should be(15)
    ScoreCalculator.calculateScore(List(6, 6, 6, 4, 1), Sixes) should be(18)
    ScoreCalculator.calculateScore(List(1, 1, 1, 2, 2), ThreeTimes) should be(7)
    ScoreCalculator.calculateScore(List(1, 1, 1, 1, 2), FourTimes) should be(6)
    ScoreCalculator.calculateScore(List(1, 1, 2, 2, 2), FullHouse) should be(25)
    ScoreCalculator.calculateScore(List(1, 2, 3, 4, 6), SmallStraight) should be(30)
    ScoreCalculator.calculateScore(List(1, 2, 3, 4, 5), LargeStraight) should be(40)
    ScoreCalculator.calculateScore(List(1, 2, 3, 4, 6), Chance) should be(16)
    ScoreCalculator.calculateScore(List(3, 3, 3, 3, 3), Kniffel) should be(50)
  }

  it should "return 0 if no dice are rolled" in {
    ScoreCalculator.calculateScore(List(), Ones) should be(0)
  }

  it should "return 0 if the strategy returns 0" in {
    ScoreCalculator.calculateScore(List(1, 2, 3, 4, 5), Kniffel) should be(0)
  }

  it should "delegate the calculation to the provided strategy" in {
    val customStrategy = new ScoringStrategyInterface {
      override def calculateScore(dice: List[Int]): Int = dice.sum * 2
    }
    ScoreCalculator.calculateScore(List(1, 2, 3, 4, 5), customStrategy) should be(30)
  }
}
