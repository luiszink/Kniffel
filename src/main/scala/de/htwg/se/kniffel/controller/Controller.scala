package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.util.Observable
import de.htwg.se.kniffel.model.{Dice, Player, ScoreCard}

class Controller extends Observable {
  var repetitions = 2
  // Model
  private var dice: Dice = new Dice()
  private var players: List[Player] = List()
  private var currentPlayerIndex: Int = 0

  def getDice = dice.values

  def keepDice(input: List[Int]) = {
    dice = dice.keepDice(input)
    repetitions = repetitions - 1
    notifyObservers("diceKept")
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
    notifyObservers("nextPlayer")
  }

  def updateScore(category: String, score: Int) = {
    val currentPlayer = getCurrentPlayer
    currentPlayer.scoreCard.categories.get(category) match {
      case Some(None) =>
        currentPlayer.scoreCard.categories += (category -> Some(score))
        notifyObservers("updateScore")
      case _ => // Do nothing if the category is already filled or doesn't exist
    }
  }
}
