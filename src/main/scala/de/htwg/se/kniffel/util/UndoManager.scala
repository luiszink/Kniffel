package de.htwg.se.kniffel.util

import com.google.inject.Inject

class UndoManager @Inject() extends UndoManagerInterface {
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def doStep(command: Command): Unit = {
    undoStack = command :: undoStack
    command.doStep
    redoStack = Nil // Clear redo stack after a new command is done
  }

  def undoStep: Unit = {
    undoStack match {
      case Nil =>
      case head :: stack =>
        head.undoStep
        undoStack = stack
        redoStack = head :: redoStack
    }
  }

  def redoStep: Unit = {
    redoStack match {
      case Nil =>
      case head :: stack =>
        head.redoStep
        redoStack = stack
        undoStack = head :: undoStack
    }
  }
}
