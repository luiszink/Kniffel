package de.htwg.se.kniffel.model.fileIoComponents.fileIoJsonImpl

import com.google.inject.Guice
import com.google.inject.name.Names
import net.codingwell.scalaguice.InjectorExtensions._
import de.htwg.se.kniffel.KniffelModule
import de.htwg.se.kniffel.model.fileIoComponents.FileIoInterface
import de.htwg.se.kniffel.model.{PlayerInterface, ScoreCardInterface}
import de.htwg.se.kniffel.model.modelImpl.{Player, ScoreCard}
import play.api.libs.json._

import scala.io.Source
import java.io.{File, PrintWriter}

class FileIoJsonImpl extends FileIoInterface {

  override def load: List[PlayerInterface] = {
    val file = new File("players.json")
    if (file.exists) {
      val source: String = Source.fromFile(file).getLines.mkString
      val json: JsValue = Json.parse(source)
      val players = (json \ "players").as[List[JsValue]]
      players.map { playerJson =>
        val name = (playerJson \ "name").as[String]
        val scoreCard = (playerJson \ "scoreCard").as[ScoreCardInterface]
        Player(name, scoreCard)
      }
    } else {
      List()
    }
  }

  override def save(players: List[PlayerInterface]): Unit = {
    val file = new File("players.json")
    val pw = new PrintWriter(file)
    pw.write(Json.prettyPrint(playersToJson(players)))
    pw.close()
  }

  implicit val scoreCardWrites: Writes[ScoreCardInterface] = new Writes[ScoreCardInterface] {
    def writes(scoreCard: ScoreCardInterface): JsValue = Json.obj(
      "categories" -> JsObject(scoreCard.categories.map {
        case (k, Some(v)) => k -> JsNumber(v)
        case (k, None)    => k -> JsNull
      }.toMap)
    )
  }

  implicit val playerWrites: Writes[PlayerInterface] = new Writes[PlayerInterface] {
    def writes(player: PlayerInterface): JsValue = Json.obj(
      "name" -> JsString(player.name),
      "scoreCard" -> Json.toJson(player.scoreCard)
    )
  }

  implicit val scoreCardReads: Reads[ScoreCardInterface] = (json: JsValue) => {
    val categories = (json \ "categories").as[Map[String, JsValue]].map {
      case (k, v) => k -> v.asOpt[Int]
    }
    JsSuccess(new ScoreCard(scala.collection.mutable.LinkedHashMap(categories.toSeq*)))
  }

  implicit val playerReads: Reads[PlayerInterface] = (json: JsValue) => {
    val name = (json \ "name").as[String]
    val scoreCard = (json \ "scoreCard").as[ScoreCardInterface]
    JsSuccess(Player(name, scoreCard))
  }

  def playersToJson(players: List[PlayerInterface]): JsValue = {
    Json.obj(
      "players" -> Json.toJson(players)
    )
  }
}
