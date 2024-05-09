package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.util.Observable
import de.htwg.se.kniffel.model.Dice

class Controller extends Observable {
  var repetitions = 2
  // Model
  private var dice: Dice = new Dice()
  def getDice = dice.values
  
  def keepDice(input: List[Int]) = {
    dice = dice.keepDice(input)
    repetitions = repetitions -1
    notifyObservers
  }
}