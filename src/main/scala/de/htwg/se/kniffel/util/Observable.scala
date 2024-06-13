package de.htwg.se.kniffel.util

trait Observer {
  def update(event: KniffelEvent.Value): Unit
}

class Observable {
  private var observers: List[Observer] = List()

  def add(observer: Observer): Unit = observers = observer :: observers

  def remove(observer: Observer): Unit = observers = observers.filterNot(o => o == observer)

  def notifyObservers(event: KniffelEvent.Value): Unit = observers.foreach(o => o.update(event))
}

object KniffelEvent extends Enumeration {
  type KniffelEvent = Value
  val PrintDice, PrintDiceUndo, PrintScoreCard, PlayerAdded, InvalidInput = Value
}