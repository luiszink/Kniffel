package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.util._
import scala.util.{Try, Success, Failure}

class Controller extends Observable {
  var repetitions = 2
  private var dice: Dice = new Dice()
  private var previousDice: Dice = _
  private var players: List[Player] = List()
  private var currentPlayerIndex: Int = 0
  private var scoreUpdater: ScoreUpdater = new StandardScoreUpdater
  private var currentState: State = new RollingState()  // Initialer Zustand
  private val undoManager = new UndoManager

  def getDice = dice.values
  def getPreviousDice = previousDice.values
  def getCurrentState = currentState
  def getCurrentPlayer: Player = players(currentPlayerIndex)
  def getPlayers: List[Player] = players


  // decide which dice to keep and change state to update the Scorecard
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

  def nextPlayer() = {
    previousDice = this.dice
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

  // is used to update the scorecard
  def updateScore(category: String): Unit = {
    val player = getCurrentPlayer
    val dice = getDice
    scoreUpdater.updateScore(player, category, dice)  // ScoreUpdater wird hier verwendet
    undoManager.doStep(new UpdateScoreCommand(player, category, dice))
    setState(new RollingState())

    if (player.scoreCard.isComplete) {
      player.scoreCard.calculateTotalScore()
      println(s"${player.name}'s total score: ${player.scoreCard.categories("totalScore").getOrElse(0)}")
    }
    
    notifyObservers("printScoreCard")
  }

  def setState(state: State): Unit = {
    currentState = state
  }

  // using the input of the player to update scorecard or keep Dices
  def handleInput(input: String): Unit = {
    if (input.toLowerCase == "undo") {
      undoManager.undoStep
      if (previousDice != null) {
        dice = previousDice
      }
      setState(new UpdateState())
      currentPlayerIndex match 
        case 0 => currentPlayerIndex = players.length-1
        case _ => currentPlayerIndex = (currentPlayerIndex - 1) % players.length
        notifyObservers("printScoreCard")
        notifyObservers("printDiceUndo")
    } else {
      currentState.handleInput(input, this)
    }
  }
}
