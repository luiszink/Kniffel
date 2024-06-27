package de.htwg.se.kniffel.model.fileIoComponents

import de.htwg.se.kniffel.model.PlayerInterface

trait FileIoInterface {
  def load: List[PlayerInterface]
  def save(players: List[PlayerInterface]): Unit
}
