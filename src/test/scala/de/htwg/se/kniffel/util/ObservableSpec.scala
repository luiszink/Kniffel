package de.htwg.se.kniffel.util

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TestObserver extends Observer {
  var notified = false
  override def update(event: KniffelEvent.Value): Unit = {
    notified = true
  }
}

class ObservableSpec extends AnyWordSpec with Matchers {

  "An Observable" should {
    "add observers correctly" in {
      val observable = new Observable
      val observer = new TestObserver

      observable.add(observer)
      observable.notifyObservers(KniffelEvent.updateScore)

      observer.notified shouldEqual true
    }

    "remove observers correctly" in {
      val observable = new Observable
      val observer = new TestObserver

      observable.add(observer)
      observable.remove(observer)
      observable.notifyObservers(KniffelEvent.updateScore)

      observer.notified shouldEqual false
    }

    "notify all observers correctly" in {
      val observable = new Observable
      val observer1 = new TestObserver
      val observer2 = new TestObserver

      observable.add(observer1)
      observable.add(observer2)
      observable.notifyObservers(KniffelEvent.updateScore)

      observer1.notified shouldEqual true
      observer2.notified shouldEqual true
    }

    "not notify removed observers" in {
      val observable = new Observable
      val observer1 = new TestObserver
      val observer2 = new TestObserver

      observable.add(observer1)
      observable.add(observer2)
      observable.remove(observer1)
      observable.notifyObservers(KniffelEvent.updateScore)

      observer1.notified shouldEqual false
      observer2.notified shouldEqual true
    }
  }
}
