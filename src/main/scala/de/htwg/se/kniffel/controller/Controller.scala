package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.util.Observable
import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.util.UndoManager
import de.htwg.se.kniffel.controller.{RollDiceCommand, UpdateScoreCommand}

class Controller extends Observable {
  var repetitions = 2
  private var dice: Dice = new Dice()
  private var players: List[Player] = List()
  private var currentPlayerIndex: Int = 0
  private var scoreUpdater: ScoreUpdater = new StandardScoreUpdater
  private var currentState: State = new RollingState()  // Initialer Zustand
  private val undoManager: UndoManager = new UndoManager()

  def getDice: List[Int] = dice.values
  def getCurrentState: State = currentState
  def getCurrentPlayer: Player = players(currentPlayerIndex)

  def setDice(newDice: Dice): Unit = {
    dice = newDice
    notifyObservers("printDice")
  }

  def keepDice(input: List[Int]): Unit = {
    val previousDice = dice
    val command = new RollDiceCommand(this, previousDice)
    undoManager.doStep(command)
    dice = dice.keepDice(input)
    repetitions -= 1
    repetitions match {
      case 0 =>
        notifyObservers("printDice")
        setState(new UpdateState())
        repetitions = 2
      case n if n > 0 => notifyObservers("printDice")
    }
  }

  def addPlayer(name: String): Unit = {
    val player = Player(name)
    players = players :+ player
    notifyObservers("playerAdded")
  }

  def nextPlayer(): Unit = {
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length
    repetitions = 2  // Reset the number of repetitions for the new player
    dice = new Dice()
    setState(new RollingState())
    notifyObservers("printScoreCard")
    notifyObservers("printDice")
  }

  def setScoreUpdater(userInput: String): Unit = {
    scoreUpdater = ScoreUpdaterFactory.createScoreUpdater(userInput)
  }

  def updateScore(category: String): Unit = {
    val previousScore = getCurrentPlayer.scoreCard.categories.get(category.toLowerCase).flatten
    val command = new UpdateScoreCommand(this, category, previousScore)
    undoManager.doStep(command)
    scoreUpdater.updateScore(getCurrentPlayer, category, dice.values)
    notifyObservers("printScoreCard")
  }

  def undo(): Unit = undoManager.undoStep()
  def redo(): Unit = undoManager.redoStep()

  def setState(state: State): Unit = {
    currentState = state
  }

  def handleInput(input: String): Unit = {
    currentState.handleInput(input, this)
  }
}
