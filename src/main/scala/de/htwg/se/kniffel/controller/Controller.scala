// Controller.scala
package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.util.Observable
import de.htwg.se.kniffel.model._

class Controller extends Observable {
  var repetitions = 2
  private var dice: Dice = new Dice()
  private var players: List[Player] = List()
  private var currentPlayerIndex: Int = 0
  private var scoreUpdater: ScoreUpdater = new StandardScoreUpdater
  private var currentState: State = new RollingState()  // Initialer Zustand

  def getDice = dice.values
  def getCurrentState = currentState

  // decide witch dice to keep an change state to update the Scorecard
  def keepDice(input: List[Int]) = {
    dice = dice.keepDice(input)
    repetitions = repetitions - 1
    repetitions match {
      case 0 => 
        notifyObservers("printDice")
        setState(new UpdateState())
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
    setState(new RollingState())
    notifyObservers("printScoreCard")
    notifyObservers("printDice")
  }

  // is used to decide how many Kniffel are possible
  def setScoreUpdater(userInput: String): Unit = {
    scoreUpdater = ScoreUpdaterFactory.createScoreUpdater(userInput)
  }

  //is used to update the scorecard
  def updateScore(category: String): Unit = {
    val player = getCurrentPlayer
    val dice = getDice
    scoreUpdater.updateScore(player, category, dice)
    setState(new RollingState())
    // print Scorecard after entered catagory
    notifyObservers("printScoreCard")
  }

  def setState(state: State): Unit = {
    currentState = state
  }
  // using the input of the player to update scorecard or keep Dices
  def handleInput(input: String): Unit = {
    currentState.handleInput(input, this)
  }
}
