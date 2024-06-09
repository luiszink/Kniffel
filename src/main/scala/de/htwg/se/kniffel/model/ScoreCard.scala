package de.htwg.se.kniffel.model

import scala.collection.mutable.LinkedHashMap

case class ScoreCard() {
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
    "totalScore" -> None
  )

  def isComplete: Boolean = categories.filterKeys(Set("one", "two", "three", "four", "five", "six",
    "threeofakind", "fourofakind", "fullhouse", "smallstraight", "largestraight", "kniffel", "chance"))
    .values.forall(_.isDefined)

  def calculateUpperSectionScore(): Unit = {
    val score = List("one", "two", "three", "four", "five", "six").flatMap(categories.get).flatten.sum
    categories("upperSectionScore") = Some(score)
  }

  def calculateBonus(): Unit = {
    calculateUpperSectionScore()
    val bonus = if (categories("upperSectionScore").getOrElse(0) >= 63) 35 else 0
    categories("bonus") = Some(bonus)
  }

  def calculateTotalScore(): Unit = {
    calculateBonus()
    val totalScore = categories.values.flatten.sum
    categories("totalScore") = Some(totalScore)
  }
}
