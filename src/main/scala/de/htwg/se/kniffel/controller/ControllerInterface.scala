package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.util._

trait ControllerInterface extends Observable {
  def repetitions: Int
  def getDice: List[Int]
  def getPreviousDice: List[Int]
  def getCurrentState: StateInterface
  def getCurrentPlayer: PlayerInterface
  def getPlayers: List[PlayerInterface]
  def keepDice(input: List[Int]): Unit
  def addPlayer(name: String): Unit
  def nextPlayer(): Unit
  def setScoreUpdater(userInput: String): Unit
  def updateScore(category: String): Unit
  def setState(state: StateInterface): Unit
  def handleInput(input: String): Unit
  def saveCurrentState(): Unit
}