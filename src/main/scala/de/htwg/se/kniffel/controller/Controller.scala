package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.util._
import scala.util.{Try, Success, Failure}

class Controller extends Observable with ControllerInterface {
  var repetitions = 2
  private var dice: DiceInterface = new Dice()
  private var previousDice: Option[DiceInterface] = None
  private var players: List[PlayerInterface] = List()
  private var currentPlayerIndex: Int = 0
  private var scoreUpdater: ScoreUpdater = new StandardScoreUpdater
  private var currentState: StateInterface = new RollingState()  // Initial state
  private val undoManager = new UndoManager

  def getDice: List[Int] = dice.values
  def getPreviousDice: List[Int] = previousDice.map(_.values).getOrElse(List())
  def getCurrentState: StateInterface = currentState
  def getCurrentPlayer: PlayerInterface = players(currentPlayerIndex)
  def getPlayers: List[PlayerInterface] = players

  // Decides which dice to keep and changes state to update the Scorecard
  def keepDice(input: List[Int]): Unit = {
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

  // Adds a new player
  def addPlayer(name: String): Unit = {
    val player: PlayerInterface = Player(name)
    players = players :+ player
    notifyObservers("playerAdded")
  }

  // Switches to the next player
  def nextPlayer(): Unit = {
    previousDice = Some(dice)
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length
    repetitions = 2  // Reset the number of repetitions for the new player
    dice = new Dice()
    setState(new RollingState())
    notifyObservers("printScoreCard")
    notifyObservers("printDice")
  }

  // Sets the ScoreUpdater based on user input
  def setScoreUpdater(userInput: String): Unit = {
    scoreUpdater = ScoreUpdaterFactory.createScoreUpdater(userInput)
  }

  // Updates the scorecard
  def updateScore(category: String): Unit = {
    val player = getCurrentPlayer
    val diceValues = getDice
    scoreUpdater.updateScore(player, category, diceValues)  // ScoreUpdater is used here
    undoManager.doStep(new UpdateScoreCommand(player, category, diceValues))
    setState(new RollingState())
    notifyObservers("printScoreCard")
  }

  // Sets the current state
  def setState(state: StateInterface): Unit = {
    currentState = state
  }

  // Handles the user input
  def handleInput(input: String): Unit = {
    if (input.toLowerCase == "undo") {
      undoManager.undoStep
      previousDice match {
        case Some(pdice) => dice = pdice
        case None =>
      }
      setState(new UpdateState())
      currentPlayerIndex = if (currentPlayerIndex == 0) players.length - 1 else (currentPlayerIndex - 1) % players.length
      notifyObservers("printScoreCard")
      notifyObservers("printDiceUndo")
    } else {
      currentState.handleInput(input, this)
    }
  }
}
