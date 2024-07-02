package de.htwg.se.kniffel.model

import scala.collection.mutable.LinkedHashMap

trait ScoreCardInterface {
  var categories: LinkedHashMap[String, Option[Int]]

  def isComplete: Boolean

  def calculateUpperSectionScore(): Unit

  def calculateLowerSectionScore(): Unit

  def calculateBonus(): Unit

  def calculateTotalScore(): Unit
}