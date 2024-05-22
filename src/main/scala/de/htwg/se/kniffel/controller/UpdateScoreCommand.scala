package de.htwg.se.kniffel.controller

import de.htwg.se.kniffel.model.Player
import de.htwg.se.kniffel.util.Command

class UpdateScoreCommand(controller: Controller, category: String, previousScore: Option[Int]) extends Command {
  private var newScore: Option[Int] = None

  override def doStep(): Unit = {
    newScore = controller.getCurrentPlayer.scoreCard.categories.get(category.toLowerCase).flatten
    controller.getCurrentPlayer.scoreCard.categories.update(category.toLowerCase, previousScore)
  }

  override def undoStep(): Unit = {
    controller.getCurrentPlayer.scoreCard.categories.update(category.toLowerCase, newScore)
  }

  override def redoStep(): Unit = {
    controller.getCurrentPlayer.scoreCard.categories.update(category.toLowerCase, previousScore)
  }
}
