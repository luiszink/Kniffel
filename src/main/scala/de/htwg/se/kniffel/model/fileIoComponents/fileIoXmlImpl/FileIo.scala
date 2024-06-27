package de.htwg.se.kniffel.model.fileIoComponents.fileIoXmlImpl

import com.google.inject.Guice
import com.google.inject.name.Names
import net.codingwell.scalaguice.InjectorExtensions._
import de.htwg.se.kniffel.KniffelModule
import de.htwg.se.kniffel.model.fileIoComponents.FileIoInterface
import de.htwg.se.kniffel.model.{PlayerInterface, ScoreCardInterface}
import de.htwg.se.kniffel.model.modelImpl.{Player, ScoreCard}
import scala.xml._
import java.io.{File, PrintWriter}

class FileIoXmlImpl extends FileIoInterface {

  override def load: List[PlayerInterface] = {
    val file = new File("players.xml")
    if (file.exists) {
      val xml = XML.loadFile(file)
      (xml \ "player").map { playerNode =>
        val name = (playerNode \ "name").text
        val scoreCard = new ScoreCard(scala.collection.mutable.LinkedHashMap(
          (playerNode \ "scoreCard" \ "category").map { categoryNode =>
            (categoryNode \ "@name").text -> (categoryNode.text match {
              case "" => None
              case x => Some(x.toInt)
            })
          }.toSeq*
        ))
        Player(name, scoreCard)
      }.toList
    } else {
      List()
    }
  }

  override def save(players: List[PlayerInterface]): Unit = {
    val file = new File("players.xml")
    val pw = new PrintWriter(file)
    val prettyPrinter = new PrettyPrinter(120, 4)
    val xml = prettyPrinter.format(playersToXml(players))
    pw.write(xml)
    pw.close
  }

  def playersToXml(players: List[PlayerInterface]): Elem = {
    <players>
      {
        players.map { player =>
          <player>
            <name>{player.name}</name>
            <scoreCard>
              {player.scoreCard.categories.map { case (name, value) =>
              <category name={name}>{value.getOrElse("")}</category>
              }}
            </scoreCard>
          </player>
        }
      }
    </players>
  }
}
