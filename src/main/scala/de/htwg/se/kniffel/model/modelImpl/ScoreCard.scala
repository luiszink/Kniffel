package de.htwg.se.kniffel.model.modelImpl

import scala.collection.mutable.LinkedHashMap
import de.htwg.se.kniffel.model.ScoreCardInterface

case class ScoreCard() extends ScoreCardInterface {
  var categories: LinkedHashMap[String, Option[Int]] = LinkedHashMap(
    "one" -> None,
    "two" -> None,
    "three" -> None,
    "four" -> None,
    "five" -> None,
    "six" -> None,
    "bonus" -> None,
    "upperSectionScore" -> None,
    "threeofakind" -> None,
    "fourofakind" -> None,
    "fullhouse" -> None,
    "smallstraight" -> None,
    "largestraight" -> None,
    "kniffel" -> None,
    "chance" -> None,
    "lowerSectionScore" -> None,
    "totalScore" -> None
  )

  def isComplete: Boolean = categories.filterKeys(Set("one", "two", "three", "four", "five", "six",
    "threeofakind", "fourofakind", "fullhouse", "smallstraight", "largestraight", "kniffel", "chance"))
    .values.forall(_.isDefined)

  def calculateUpperSectionScore(): Unit = {
    val score = List("one", "two", "three", "four", "five", "six").flatMap(categories.get).flatten.sum
    categories("upperSectionScore") = Some(score)
  }

  def calculateLowerSectionScore(): Unit = {
    val score = List("threeofakind", "fourofakind", "fullhouse", "smallstraight", "largestraight", "kniffel", "chance").flatMap(categories.get).flatten.sum
    categories("lowerSectionScore") = Some(score)
  }

  def calculateBonus(): Unit = {
    calculateUpperSectionScore()
    val bonus = if (categories("upperSectionScore").getOrElse(0) >= 63) 35 else 0
    categories("bonus") = Some(bonus)
  }

  def calculateTotalScore(): Unit = {
    calculateUpperSectionScore()
    calculateLowerSectionScore()
    calculateBonus()
    val totalScore = categories("upperSectionScore").getOrElse(0) + categories("bonus").getOrElse(0) + categories("lowerSectionScore").getOrElse(0)
    categories("totalScore") = Some(totalScore)
  }
}
