package de.htwg.se.kniffel.model.fileIoComponents.fileIoXmlImpl

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.kniffel.model.{PlayerInterface, ScoreCardInterface}
import de.htwg.se.kniffel.model.modelImpl.{Player, ScoreCard}
import scala.xml._
import java.io.{File, PrintWriter}

class FileIoXmlImplSpec extends AnyFlatSpec with Matchers {

  "FileIoXmlImpl" should "save players to an XML file" in {
    val fileIo = new FileIoXmlImpl
    val player1 = Player("Player1", new ScoreCard())
    val player2 = Player("Player2", new ScoreCard())
    val players = List(player1, player2)

    // Save players
    fileIo.save(players)

    // Verify file content
    val file = new File("players.xml")
    file.exists() should be(true)
    val xml = XML.loadFile(file)
    (xml \\ "player").size should be(2)

    // Clean up
    file.delete()
  }

  it should "load players from an XML file" in {
    val fileIo = new FileIoXmlImpl
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
    new File("players.xml").delete()
  }

  it should "return an empty list if the XML file does not exist" in {
    val fileIo = new FileIoXmlImpl
    val file = new File("players.xml")
    if (file.exists()) file.delete()

    val loadedPlayers = fileIo.load
    loadedPlayers should be(empty)
  }

  "XML conversions" should "convert ScoreCardInterface to XML and back" in {
    val fileIo = new FileIoXmlImpl
    val scoreCard = new ScoreCard()
    val player = Player("TestPlayer", scoreCard)
    val xml = fileIo.playersToXml(List(player))
    val loadedPlayers = (xml \\ "player").map { playerNode =>
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

    loadedPlayers.size should be(1)
    loadedPlayers.head.name should be("TestPlayer")
    loadedPlayers.head.scoreCard.categories.toMap shouldEqual scoreCard.categories.toMap
  }

  it should "correctly convert Some(x.toInt) in XML" in {
    val fileIo = new FileIoXmlImpl
    val scoreCard = new ScoreCard(scala.collection.mutable.LinkedHashMap(
      "one" -> Some(1),
      "two" -> Some(2),
      "three" -> Some(3),
      "four" -> Some(4),
      "five" -> Some(5),
      "six" -> Some(6),
      "threeofakind" -> Some(10),
      "fourofakind" -> Some(20),
      "fullhouse" -> Some(25),
      "smallstraight" -> Some(30),
      "largestraight" -> Some(40),
      "kniffel" -> Some(50),
      "chance" -> Some(15),
      "bonus" -> Some(35),
      "upperSectionScore" -> Some(63),
      "lowerSectionScore" -> Some(150),
      "totalScore" -> Some(248)
    ))
    val player = Player("TestPlayer", scoreCard)
    val xml = fileIo.playersToXml(List(player))
    val loadedPlayers = (xml \\ "player").map { playerNode =>
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

    loadedPlayers.size should be(1)
    loadedPlayers.head.name should be("TestPlayer")
    loadedPlayers.head.scoreCard.categories("one") shouldEqual Some(1)
    loadedPlayers.head.scoreCard.categories("two") shouldEqual Some(2)
    loadedPlayers.head.scoreCard.categories("three") shouldEqual Some(3)
    loadedPlayers.head.scoreCard.categories("four") shouldEqual Some(4)
    loadedPlayers.head.scoreCard.categories("five") shouldEqual Some(5)
    loadedPlayers.head.scoreCard.categories("six") shouldEqual Some(6)
    loadedPlayers.head.scoreCard.categories("threeofakind") shouldEqual Some(10)
    loadedPlayers.head.scoreCard.categories("fourofakind") shouldEqual Some(20)
    loadedPlayers.head.scoreCard.categories("fullhouse") shouldEqual Some(25)
    loadedPlayers.head.scoreCard.categories("smallstraight") shouldEqual Some(30)
    loadedPlayers.head.scoreCard.categories("largestraight") shouldEqual Some(40)
    loadedPlayers.head.scoreCard.categories("kniffel") shouldEqual Some(50)
    loadedPlayers.head.scoreCard.categories("chance") shouldEqual Some(15)
    loadedPlayers.head.scoreCard.categories("bonus") shouldEqual Some(35)
    loadedPlayers.head.scoreCard.categories("upperSectionScore") shouldEqual Some(63)
    loadedPlayers.head.scoreCard.categories("lowerSectionScore") shouldEqual Some(150)
    loadedPlayers.head.scoreCard.categories("totalScore") shouldEqual Some(248)
  }
}
