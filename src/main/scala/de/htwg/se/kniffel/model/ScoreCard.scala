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
    "threeofakind" -> None,
    "fourofakind" -> None,
    "fullhouse" -> None,
    "smallstraight" -> None,
    "largestraight" -> None,
    "kniffel" -> None,
    "chance" -> None
  )
 /* def totalScore: Int = {
    categories.values.flatten.sum
 } */
}
