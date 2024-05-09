package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.model.Dice
import de.htwg.se.kniffel.util.{Observer, Observable}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ControllerTest extends AnyWordSpec with Matchers {
  "A Controller" when {
    "used to play Kniffel" should {
      "correctly manage repetitions and dice" in {
        val controller = new Controller()
        controller.repetitions shouldBe 2

        controller.keepDice(List(1, 2, 3))
        controller.repetitions shouldBe 1
        controller.getDice.size shouldBe 5
      }
    }
  }
}
