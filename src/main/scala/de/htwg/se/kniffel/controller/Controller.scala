package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.model.Player
import de.htwg.se.kniffel.model.ScoreUpdaterFactory
import de.htwg.se.kniffel.model.StandardScoreUpdater
import de.htwg.se.kniffel.model.Dice
import de.htwg.se.kniffel.model.Threes
import de.htwg.se.kniffel.model.Fours
import de.htwg.se.kniffel.model.Fives
import de.htwg.se.kniffel.model.Sixes
import de.htwg.se.kniffel.model.ThreeTimes
import de.htwg.se.kniffel.model.FourTimes
import de.htwg.se.kniffel.model.FullHouse
import de.htwg.se.kniffel.model.SmallStraight
import de.htwg.se.kniffel.model.LargeStraight
import de.htwg.se.kniffel.model.Chance
import de.htwg.se.kniffel.model.Kniffel
import de.htwg.se.kniffel.util.Observable
import de.htwg.se.kniffel.util.UndoManager
import de.htwg.se.kniffel.util.KniffelEvent
import scala.util.{Try, Success, Failure}
import de.htwg.se.kniffel.model.ScoreUpdater

class Controller extends Observable {
  var repetitions = 2
  private var dice: Dice = new Dice()
  private var previousDice: Dice = _
  private var players: List[Player] = List()
  private var currentPlayerIndex: Int = 0
  private var scoreUpdater: ScoreUpdater = new StandardScoreUpdater
  private var currentState: State = new RollingState()
  private val undoManager = new UndoManager

  def getDice = dice.values
  def getPreviousDice = previousDice.values
  def getCurrentState = currentState
  def getCurrentPlayer: Player = players(currentPlayerIndex)
  def getPlayers: List[Player] = players

  def keepDice(input: List[Int]) = {
    dice = dice.keepDice(input)
    repetitions = repetitions - 1
    repetitions match {
      case 0 => 
        notifyObservers(KniffelEvent.PrintDice)
        setState(new UpdateState())
        repetitions = 2
      case n if n > 0 => notifyObservers(KniffelEvent.PrintDice)
    }
  }

  def addPlayer(name: String) = {
    val player = Player(name)
    players = players :+ player
    notifyObservers(KniffelEvent.PlayerAdded)
  }

  def nextPlayer() = {
    previousDice = this.dice
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length
    repetitions = 2
    dice = new Dice()
    setState(new RollingState())
    notifyObservers(KniffelEvent.PrintScoreCard)
    notifyObservers(KniffelEvent.PrintDice)
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

  def setState(state: State): Unit = {
    currentState = state
  }

  def handleInput(input: String): Unit = {
    Try {
      if (input.toLowerCase == "undo") {
        undoManager.undoStep
        if (previousDice != null) {
          dice = previousDice
        }
        setState(new UpdateState())
        currentPlayerIndex match 
          case 0 => currentPlayerIndex = players.length-1
          case _ => currentPlayerIndex = (currentPlayerIndex - 1) % players.length
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