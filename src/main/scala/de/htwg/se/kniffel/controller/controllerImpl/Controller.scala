package de.htwg.se.kniffel.controller.controllerImpl

import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.controller.{StateInterface, ControllerInterface}
import de.htwg.se.kniffel.util._
import scala.util.{Try, Success, Failure}
import de.htwg.se.kniffel.model.scoreUpdaterImpl._
import de.htwg.se.kniffel.model.modelImpl._
import com.google.inject.{Inject, Provider}

class Controller @Inject() extends Observable with ControllerInterface {  
  var repetitions = 2
  private var dice: DiceInterface = Dice(List.fill(5)(Dice.rollDice()))
  private var previousDice: Option[DiceInterface] = None
  private var players: List[PlayerInterface] = List()
  private var currentPlayerIndex: Int = 0
  private var scoreUpdater: ScoreUpdaterInterface = new StandardScoreUpdater
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

  def addPlayer(name: String): Unit = {
    val player: PlayerInterface = Player(name)
    players = players :+ player
    notifyObservers(KniffelEvent.PlayerAdded)
  }

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

  def setScoreUpdater(userInput: String): Unit = {
    scoreUpdater = ScoreUpdaterFactory.createScoreUpdater(userInput)
  }

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
