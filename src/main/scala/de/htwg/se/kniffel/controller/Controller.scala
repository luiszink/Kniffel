package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.util._
import scala.util.{Try, Success, Failure}

class Controller extends Observable with ControllerInterface {
  var repetitions = 2
  private var dice: DiceInterface = Dice(List.fill(5)(Dice.rollDice()))
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

  def rollDice(): Unit = {
    dice = Dice(List.fill(5)(Dice.rollDice()))
  }
  // Decides which dice to keep and changes state to update the Scorecard
  def keepDice(input: List[Int]): Unit = {
    dice = dice.keepDice(input)
    repetitions -= 1
    repetitions match {
      case 0 => 
        notifyObservers(KniffelEvent.PrintDice)
        setState(new UpdateState())
        repetitions = 2
      case n if n > 0 => notifyObservers(KniffelEvent.PrintDice)
    }
  }

  // Adds a new player
  def addPlayer(name: String): Unit = {
    val player: PlayerInterface = Player(name)
    players = players :+ player
    notifyObservers(KniffelEvent.PlayerAdded)
  }

  // Switches to the next player
  def nextPlayer(): Unit = {
    previousDice = Some(dice)
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length
    repetitions = 2
    dice = new Dice(List.fill(5)(Dice.rollDice()))
    setState(new RollingState())
    notifyObservers(KniffelEvent.PrintScoreCard)
    notifyObservers(KniffelEvent.PrintDice)
    notifyObservers(KniffelEvent.NextPlayer) // Notify GUI to reset selected dice
  }

  // Sets the ScoreUpdater based on user input
  def setScoreUpdater(userInput: String): Unit = {
    scoreUpdater = ScoreUpdaterFactory.createScoreUpdater(userInput)
  }

  // Updates the scorecard
  def updateScore(category: String): Unit = {
    val player = getCurrentPlayer
    val dice = getDice
    scoreUpdater.updateScore(player, category, dice)
    undoManager.doStep(new UpdateScoreCommand(player, category, dice))
    player.scoreCard.isComplete match {
      case true =>
        player.scoreCard.calculateTotalScore()
        println(s"${player.name}'s total score: ${player.scoreCard.categories("totalScore").getOrElse(0)}")
      case false =>
    }
    nextPlayer()
  }

  // Sets the current state
  def setState(state: StateInterface): Unit = {
    currentState = state
  }

  def handleInput(input: String): Unit = {
    Try {
      if (input.toLowerCase == "undo") {
        undoManager.undoStep
        previousDice match {
          case Some(pdice) => dice = pdice
          case None => // Do nothing
        }
        setState(new UpdateState())
        currentPlayerIndex match {
          case 0 => currentPlayerIndex = players.length - 1
          case _ => currentPlayerIndex = (currentPlayerIndex - 1) % players.length
        }
        notifyObservers(KniffelEvent.PrintScoreCard)
        notifyObservers(KniffelEvent.PrintDiceUndo)
      } else {
        currentState.handleInput(input, this)
      }
    } match {
      case Success(_) =>
      case Failure(_) => 
        notifyObservers(KniffelEvent.InvalidInput)
    }
  }
}
