package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.util.Observable
import de.htwg.se.kniffel.model.{Dice, Player, ScoreCard, ScoreCalculator, ScoringStrategy}
import de.htwg.se.kniffel.model.{Ones, Twos, Threes, Fours, Fives, Sixes, ThreeTimes, FourTimes, FullHouse, SmallStraight, LargeStraight, Chance, Kniffel}

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

  def updateScore(category: String): Unit = {
    val currentPlayer = getCurrentPlayer
    val strategy: ScoringStrategy = category.toLowerCase match {
      case "one" => Ones
      case "two" => Twos
      case "three" => Threes
      case "four" => Fours
      case "five" => Fives
      case "sixe" => Sixes
      case "threetime" => ThreeTimes
      case "fourtime" => FourTimes
      case "fullhouse" => FullHouse
      case "smallstraight" => SmallStraight
      case "largestraight" => LargeStraight
      case "chance" => Chance
      case "kniffel" => Kniffel
      // has to be change to get another input try
      case _ => throw new IllegalArgumentException("Invalid category.")
    }
    val calculatedScore = ScoreCalculator.calculateScore(getDice, strategy) // Hier verwenden wir ScoreCalculator.calculateScore
    currentPlayer.scoreCard.categories.get(category) match {
      case None =>
        currentPlayer.scoreCard.categories += (category -> Some(calculatedScore))
        notifyObservers("printScoreCard")
      case _ => 
        println(calculatedScore)
        println(category)// Do nothing if the category is already filled or doesn't exist
    }
  }



}
