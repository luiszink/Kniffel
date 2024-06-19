package de.htwg.se.kniffel

import scala.io.StdIn
import scala.util.Random
import de.htwg.se.kniffel.model.Dice
import de.htwg.se.kniffel.controller.{Controller, ControllerInterface}
import scala.concurrent.{Future, Await}
import de.htwg.se.kniffel.aview.GUI
import de.htwg.se.kniffel.aview.TUI
import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.kniffel.model.ScoringStrategy
import com.google.inject.name.Named

class ScoreService @Inject() (
  @Named("Ones") ones: ScoringStrategy,
  @Named("Twos") twos: ScoringStrategy,
  @Named("Threes") threes: ScoringStrategy,
  @Named("Fours") fours: ScoringStrategy,
  @Named("Fives") fives: ScoringStrategy,
  @Named("Sixes") sixes: ScoringStrategy,
  @Named("ThreeTimes") threeTimes: ScoringStrategy,
  @Named("FourTimes") fourTimes: ScoringStrategy,
  @Named("FullHouse") fullHouse: ScoringStrategy,
  @Named("SmallStraight") smallStraight: ScoringStrategy,
  @Named("LargeStraight") largeStraight: ScoringStrategy,
  @Named("Chance") chance: ScoringStrategy,
  @Named("Kniffel") kniffel: ScoringStrategy
) {
  def calculateOnesScore(dice: List[Int]): Int = ones.calculateScore(dice)
  def calculateTwosScore(dice: List[Int]): Int = twos.calculateScore(dice)
  def calculateThreesScore(dice: List[Int]): Int = threes.calculateScore(dice)
  def calculateFoursScore(dice: List[Int]): Int = fours.calculateScore(dice)
  def calculateFivesScore(dice: List[Int]): Int = fives.calculateScore(dice)
  def calculateSixesScore(dice: List[Int]): Int = sixes.calculateScore(dice)
  def calculateThreeTimesScore(dice: List[Int]): Int = threeTimes.calculateScore(dice)
  def calculateFourTimesScore(dice: List[Int]): Int = fourTimes.calculateScore(dice)
  def calculateFullHouseScore(dice: List[Int]): Int = fullHouse.calculateScore(dice)
  def calculateSmallStraightScore(dice: List[Int]): Int = smallStraight.calculateScore(dice)
  def calculateLargeStraightScore(dice: List[Int]): Int = largeStraight.calculateScore(dice)
  def calculateChanceScore(dice: List[Int]): Int = chance.calculateScore(dice)
  def calculateKniffelScore(dice: List[Int]): Int = kniffel.calculateScore(dice)
}

object KniffelApp {
  def main(args: Array[String]): Unit = {
    val injector: Injector = Guice.createInjector(new KniffelModule)
    val scoreService: ScoreService = injector.getInstance(classOf[ScoreService])
    val controller: ControllerInterface = injector.getInstance(classOf[ControllerInterface])
    val tui: TUI = new TUI(controller)
    val gui: GUI = new GUI(controller)

    implicit val context = scala.concurrent.ExecutionContext.global
    val f = Future {
      gui.main(Array[String]())
    }
    tui.run()
    Await.ready(f, scala.concurrent.duration.Duration.Inf)
  }
}
