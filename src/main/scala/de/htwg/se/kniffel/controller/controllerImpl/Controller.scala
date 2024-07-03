package de.htwg.se.kniffel.controller.controllerImpl

import de.htwg.se.kniffel.model._
import de.htwg.se.kniffel.controller.{StateInterface, ControllerInterface}
import de.htwg.se.kniffel.util._
import scala.util.{Try, Success, Failure}
import de.htwg.se.kniffel.model.scoreUpdaterImpl._
import de.htwg.se.kniffel.model.modelImpl._
import de.htwg.se.kniffel.model.fileIoComponents.FileIoInterface
import de.htwg.se.kniffel.model.fileIoComponents.fileIoJsonImpl.FileIoJsonImpl
import de.htwg.se.kniffel.model.fileIoComponents.fileIoXmlImpl.FileIoXmlImpl
import com.google.inject.{Inject, Provider}

class Controller @Inject() (
    jsonProvider: Provider[FileIoJsonImpl],
    xmlProvider: Provider[FileIoXmlImpl]
) extends Observable
    with ControllerInterface {

  var repetitions = 2
  private var dice: DiceInterface = Dice(List.fill(5)(Dice.rollDice()))
  private var previousDice: Option[DiceInterface] = None
  private var players: List[PlayerInterface] = List()
  private var currentPlayerIndex: Int = 0
  private var scoreUpdater: ScoreUpdaterInterface = new StandardScoreUpdater
  private var currentState: StateInterface = new RollingState()
  private val undoManager = new UndoManager

  private val fileIoJson: FileIoInterface = jsonProvider.get()
  private val fileIoXml: FileIoInterface = xmlProvider.get()

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
        notifyObservers(KniffelEvent.keepDice)
        setState(new UpdateState())
        notifyObservers(KniffelEvent.noRepetitions)
      case n if n > 0 =>
        notifyObservers(KniffelEvent.keepDice)
    }
  }

  // Neue Methode um den Gewinner zu ermitteln
  def getWinner: String = {
    val winner = players.maxBy(_.getTotalScore)
    winner.name
  }

  // Neue Methode um die Endergebnisse zu erhalten
  def getFinalScores: List[String] = {
    players.map(player => s"${player.name}: ${player.getTotalScore}")
  }

  def addPlayer(name: String): Unit = {
    val player: PlayerInterface = Player(name)
    players = players :+ player
    saveCurrentState()
    notifyObservers(KniffelEvent.PlayerAdded)
  }

  def checkGameEnd(): Unit = {
    if (players.forall(_.scoreCard.isComplete)) {
      notifyObservers(KniffelEvent.GameEnded)
    }
  }

  def nextPlayer(): Unit = {
    previousDice = Some(dice)
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length
    checkGameEnd()
    repetitions = 2
    dice = new Dice(List.fill(5)(Dice.rollDice()))
    setState(new RollingState())
    notifyObservers(KniffelEvent.NextPlayer)
  }

  def setScoreUpdater(userInput: String): Unit = {
    scoreUpdater = ScoreUpdaterFactory.createScoreUpdater(userInput)
    notifyObservers(KniffelEvent.setScoreUpdater)
  }

  def updateScore(category: String): Unit = {
    val player = getCurrentPlayer
    val dice = getDice
    val prevDice = previousDice.getOrElse(this.dice)
    val command = new UpdateScoreCommand(player, category, dice, prevDice)
    undoManager.doStep(command)
    player.scoreCard.isComplete match {
      case true =>
        player.scoreCard.calculateTotalScore()
      case false =>
    }
    repetitions = 2
    saveCurrentState()
    nextPlayer()
    notifyObservers(KniffelEvent.updateScore)
  }

  def setState(state: StateInterface): Unit = {
    currentState = state
  }

  def handleInput(input: String): Unit = {
    Try {
      if (input.toLowerCase == "undo") {
        undoManager.undoStep
        notifyObservers(KniffelEvent.Undo)
      } else {
        currentState.handleInput(input, this)
      }
    } match {
      case Success(_) =>
      case Failure(_) =>
        notifyObservers(KniffelEvent.InvalidInput)
    }
  }

  // Methode zum Speichern des aktuellen Zustands
  def saveCurrentState(): Unit = {
    fileIoJson.save(players)
    fileIoXml.save(players)
  }

  // Neue Methoden für Tests
  def isStandardScoreUpdater: Boolean = scoreUpdater.isInstanceOf[StandardScoreUpdater]
  def isMultiKniffelScoreUpdater: Boolean = scoreUpdater.isInstanceOf[MultiKniffelScoreUpdater]
}
