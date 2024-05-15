package de.htwg.se.kniffel.model

import scala.io.StdIn
import scala.collection.mutable.SortedMap

case class ScoreCard() {
  var categories: SortedMap[String, Option[Int]] = SortedMap(
    "One" -> None,
    "Two" -> None,
    "Three" -> None,
    "Four" -> None,
    "Five" -> None,
    "Sixe" -> None,
    "ThreeOfAKind" -> None,
    "FourOfAKind" -> None,
    "FullHouse" -> None,
    "SmallStraight" -> None,
    "LargeStraight" -> None,
    "Yahtzee" -> None,
    "Chance" -> None
  )
}
