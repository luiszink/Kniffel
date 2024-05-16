package de.htwg.se.kniffel.util

trait Observer {
  def update(message: String): Unit
}

class Observable {
  private var observers: List[Observer] = List()

  def add(observer: Observer): Unit = observers = observer :: observers

  def remove(observer: Observer): Unit = observers = observers.filterNot(o => o == observer)

  def notifyObservers(message: String): Unit = observers.foreach(o => o.update(message))
}
