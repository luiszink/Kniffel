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

  "A ScoreCard" should "calculate the upper section score correctly" in {
    val scoreCard = new ScoreCard()
    scoreCard.categories.update("one", Some(3))
    scoreCard.categories.update("two", Some(6))
    scoreCard.categories.update("three", Some(9))
    scoreCard.categories.update("four", Some(12))
    scoreCard.categories.update("five", Some(15))
    scoreCard.categories.update("six", Some(18))
    scoreCard.calculateUpperSectionScore()
    scoreCard.categories("upperSectionScore") should be(Some(63))
  }

  it should "calculate the lower section score correctly" in {
    val scoreCard = new ScoreCard()
    scoreCard.categories.update("threeofakind", Some(18))
    scoreCard.categories.update("fourofakind", Some(24))
    scoreCard.categories.update("fullhouse", Some(25))
    scoreCard.categories.update("smallstraight", Some(30))
    scoreCard.categories.update("largestraight", Some(40))
    scoreCard.categories.update("kniffel", Some(50))
    scoreCard.categories.update("chance", Some(20))
    scoreCard.calculateLowerSectionScore()
    scoreCard.categories("lowerSectionScore") should be(Some(207))
  }

  it should "calculate the bonus correctly when upper section score is 63 or more" in {
    val scoreCard = new ScoreCard()
    scoreCard.categories.update("one", Some(3))
    scoreCard.categories.update("two", Some(6))
    scoreCard.categories.update("three", Some(9))
    scoreCard.categories.update("four", Some(12))
    scoreCard.categories.update("five", Some(15))
    scoreCard.categories.update("six", Some(18))
    scoreCard.calculateBonus()
    scoreCard.categories("bonus") should be(Some(35))
  }

  it should "not calculate the bonus when upper section score is less than 63" in {
    val scoreCard = new ScoreCard()
    scoreCard.categories.update("one", Some(1))
    scoreCard.categories.update("two", Some(2))
    scoreCard.categories.update("three", Some(3))
    scoreCard.categories.update("four", Some(4))
    scoreCard.categories.update("five", Some(5))
    scoreCard.categories.update("six", Some(6))
    scoreCard.calculateBonus()
    scoreCard.categories("bonus") should be(Some(0))
  }

  it should "calculate the total score correctly" in {
    val scoreCard = new ScoreCard()
    scoreCard.categories.update("one", Some(3))
    scoreCard.categories.update("two", Some(6))
    scoreCard.categories.update("three", Some(9))
    scoreCard.categories.update("four", Some(12))
    scoreCard.categories.update("five", Some(15))
    scoreCard.categories.update("six", Some(18))
    scoreCard.categories.update("threeofakind", Some(18))
    scoreCard.categories.update("fourofakind", Some(24))
    scoreCard.categories.update("fullhouse", Some(25))
    scoreCard.categories.update("smallstraight", Some(30))
    scoreCard.categories.update("largestraight", Some(40))
    scoreCard.categories.update("kniffel", Some(50))
    scoreCard.categories.update("chance", Some(20))
    scoreCard.calculateTotalScore()
    scoreCard.categories("totalScore") should be(Some(305))
  }

  it should "calculate the total score correctly with bonus" in {
    val scoreCard = new ScoreCard()
    scoreCard.categories.update("one", Some(3))
    scoreCard.categories.update("two", Some(6))
    scoreCard.categories.update("three", Some(9))
    scoreCard.categories.update("four", Some(12))
    scoreCard.categories.update("five", Some(15))
    scoreCard.categories.update("six", Some(18))
    scoreCard.categories.update("threeofakind", Some(18))
    scoreCard.categories.update("fourofakind", Some(24))
    scoreCard.categories.update("fullhouse", Some(25))
    scoreCard.categories.update("smallstraight", Some(30))
    scoreCard.categories.update("largestraight", Some(40))
    scoreCard.categories.update("kniffel", Some(50))
    scoreCard.categories.update("chance", Some(20))
    scoreCard.calculateTotalScore()
    scoreCard.categories("totalScore") should be(Some(305))
    scoreCard.categories("bonus") should be(Some(35))
  }
}
