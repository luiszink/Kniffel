package de.htwg.se.kniffel.model.fileIoComponents.fileIoJsonImpl

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.kniffel.model.{PlayerInterface, ScoreCardInterface}
import de.htwg.se.kniffel.model.modelImpl.{Player, ScoreCard}
import play.api.libs.json._
import java.io.{File, PrintWriter}
import scala.io.Source

class FileIoJsonImplSpec extends AnyFlatSpec with Matchers {

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

  "FileIoJsonImpl" should "save players to a JSON file" in {
    val fileIo = new FileIoJsonImpl
    val player1 = Player("Player1", new ScoreCard())
    val player2 = Player("Player2", new ScoreCard())
    val players = List(player1, player2)

    // Save players
    fileIo.save(players)

    // Verify file content
    val file = new File("players.json")
    file.exists() should be(true)
    val source = Source.fromFile(file).getLines.mkString
    val json = Json.parse(source)
    (json \ "players").as[List[JsValue]].size should be(2)

    // Clean up
    file.delete()
  }

  it should "load players from a JSON file" in {
    val fileIo = new FileIoJsonImpl
    val player1 = Player("Player1", new ScoreCard())
    val player2 = Player("Player2", new ScoreCard())
    val players = List(player1, player2)

    // Save players
    fileIo.save(players)

    // Load players
    val loadedPlayers = fileIo.load
    loadedPlayers.size should be(2)
    loadedPlayers.head.name should be("Player1")
    loadedPlayers(1).name should be("Player2")

    // Clean up
    new File("players.json").delete()
  }

  it should "return an empty list if the JSON file does not exist" in {
    val fileIo = new FileIoJsonImpl
    val file = new File("players.json")
    if (file.exists()) file.delete()

    val loadedPlayers = fileIo.load
    loadedPlayers should not be(empty)
  }

}
