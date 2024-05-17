// Controller.scala
package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.util.Observable
import de.htwg.se.kniffel.model._
//import de.htwg.se.kniffel.model.{Dice, Player, ScoreCard, ScoreCalculator, ScoringStrategy, ScoreUpdaterFactory}
//import de.htwg.se.kniffel.model.{Ones, Twos, Threes, Fours, Fives, Sixes, ThreeTimes, FourTimes, FullHouse, SmallStraight, LargeStraight, Chance, Kniffel}

class Controller extends Observable {
  var repetitions = 2
  // Model
  private var dice: Dice = new Dice()
  private var players: List[Player] = List()
  private var currentPlayerIndex: Int = 0
  private var scoreUpdater: ScoreUpdater = _

  def getDice = dice.values

  def keepDice(input: List[Int]) = {
    dice = dice.keepDice(input)
    repetitions = repetitions - 1
    repetitions match {
      case 0 => 
        notifyObservers("printDice")
        notifyObservers("updateScore")
        nextPlayer()
        repetitions = 2
      case n if n > 0 => notifyObservers("printDice")
    }
  }

  def addPlayer(name: String) = {
    val player = Player(name)
    players = players :+ player
    notifyObservers("playerAdded")
  }

  def getCurrentPlayer: Player = players(currentPlayerIndex)

  def nextPlayer() = {
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length
    repetitions = 2  // Reset the number of repetitions for the new player
    dice = new Dice()
    notifyObservers("")
  }

  def setScoreUpdater(userInput: String): Unit = {
    scoreUpdater = ScoreUpdaterFactory.createScoreUpdater(userInput)
  }

  def updateScore(category: String): Unit = {
    val player = getCurrentPlayer
    val dice = getDice
    scoreUpdater.updateScore(player, category, dice)
    notifyObservers("printScoreCard")
  }
}

