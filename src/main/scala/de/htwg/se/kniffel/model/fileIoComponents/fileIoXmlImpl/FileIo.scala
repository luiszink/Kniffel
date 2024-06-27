package de.htwg.se.kniffel.model.fileIoComponents.fileIoXmlImpl

import com.google.inject.Guice
import com.google.inject.name.Names
import net.codingwell.scalaguice.InjectorExtensions._
import de.htwg.se.kniffel.KniffelModule
import de.htwg.se.kniffel.model.fileIoComponents.FileIoInterface
import de.htwg.se.kniffel.model.{PlayerInterface, ScoreCardInterface}
import de.htwg.se.kniffel.model.modelImpl.{Player, ScoreCard}
import scala.xml._

class FileIoXmlImpl extends FileIoInterface {

  override def load: List[PlayerInterface] = {
    val xml = XML.loadFile("players.xml")
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
  }

  override def save(players: List[PlayerInterface]): Unit = {
    import java.io._
    val pw = new PrintWriter(new File("players.xml"))
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
