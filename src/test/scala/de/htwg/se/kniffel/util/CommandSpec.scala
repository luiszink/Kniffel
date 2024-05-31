package de.htwg.se.kniffel.util

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class DummyCommand extends Command {
  var didDoStep = false
  var didUndoStep = false
  var didRedoStep = false

  override def doStep: Unit = didDoStep = true
  override def undoStep: Unit = didUndoStep = true
  override def redoStep: Unit = didRedoStep = true
}

class UndoManagerSpec extends AnyWordSpec with Matchers {

  "An UndoManager" should {

    "execute a command with doStep" in {
      val undoManager = new UndoManager
      val command = new DummyCommand

      undoManager.doStep(command)
      command.didDoStep shouldEqual true
      command.didUndoStep shouldEqual false
      command.didRedoStep shouldEqual false
    }

    "undo a command with undoStep" in {
      val undoManager = new UndoManager
      val command = new DummyCommand

      undoManager.doStep(command)
      undoManager.undoStep
      command.didDoStep shouldEqual true
      command.didUndoStep shouldEqual true
      command.didRedoStep shouldEqual false
    }

    "redo a command with redoStep" in {
      val undoManager = new UndoManager
      val command = new DummyCommand

      undoManager.doStep(command)
      undoManager.undoStep
      undoManager.redoStep
      command.didDoStep shouldEqual true
      command.didUndoStep shouldEqual true
      command.didRedoStep shouldEqual true
    }

    "clear redo stack after a new command is done" in {
      val undoManager = new UndoManager
      val command1 = new DummyCommand
      val command2 = new DummyCommand

      undoManager.doStep(command1)
      undoManager.undoStep
      undoManager.redoStep
      undoManager.doStep(command2)

      command1.didDoStep shouldEqual true
      command1.didUndoStep shouldEqual true
      command1.didRedoStep shouldEqual true

      command2.didDoStep shouldEqual true
      command2.didUndoStep shouldEqual false
      command2.didRedoStep shouldEqual false

      // Ensure redo stack is cleared
      undoManager.redoStep
      command2.didRedoStep shouldEqual false
    }

    "handle undo with an empty undo stack" in {
      val undoManager = new UndoManager

      // Attempt to undo with an empty stack should not throw an error
      noException should be thrownBy undoManager.undoStep
    }

    "handle redo with an empty redo stack" in {
      val undoManager = new UndoManager

      // Attempt to redo with an empty stack should not throw an error
      noException should be thrownBy undoManager.redoStep
    }
  }
}
