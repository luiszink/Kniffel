package de.htwg.se.kniffel.aview

import de.htwg.se.kniffel.controller.Controller
import de.htwg.se.kniffel.util.Observer
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{Pane, VBox, HBox}
import scalafx.scene.control.{TableView, TableColumn, Button, Label, CheckBox}
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.application.Platform
import scalafx.stage.Screen
import scalafx.geometry.{Insets, Pos}
import scala.compiletime.uninitialized
import javafx.stage._


class GUI(controller: Controller) extends JFXApp3 with Observer {
  controller.add(this)

  var tableView: TableView[(String, String)] = uninitialized
  var rollButton: Button = uninitialized
  var diceResultsLabel: Label = uninitialized
  var diceCheckBoxes: Seq[CheckBox] = uninitialized

  override def start(): Unit = {
    try {
      val screenBounds = Screen.primary.visualBounds

      stage = new JFXApp3.PrimaryStage {
        title = "Kniffel"
        resizable = true
        scene = new Scene(screenBounds.width, screenBounds.height) {

          val pane = new Pane()

          tableView = new TableView[(String, String)]()

          rollButton = new Button("Würfeln")
          rollButton.onAction = _ => rollDice()

          diceResultsLabel = new Label("Würfelergebnisse: ")

          diceCheckBoxes = (1 to 5).map { i =>
            new CheckBox(s"Dice $i")
          }

          val diceBox = new VBox(10) {
            children = Seq(rollButton, diceResultsLabel) ++ diceCheckBoxes
            padding = Insets(20)
            alignment = Pos.Center
          }

          val hbox = new HBox(20) {
            children = Seq(tableView, diceBox)
            padding = Insets(20)
            alignment = Pos.Center
          }
          hbox.layoutX = 50
          hbox.layoutY <== (pane.height - hbox.height) / 2

          pane.children = hbox
          content = pane
        }

      }

    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

  override def update(message: String): Unit = {
    Platform.runLater(message match {
      case "printScoreCard" => scorecard()
      case "playerAdded"    => scorecard()
      case "printDice"      => updateDiceResults()
      case _                => println("")
    })
  }

  def scorecard(): Unit = {
    tableView.columns.clear()
    val score = controller.getCurrentPlayer.scoreCard
    val players = controller.getPlayers
    val categoryColumn =
      new TableColumn[(String, String), String]("Category") {
        cellValueFactory = { cellData =>
          new StringProperty(this, "category", cellData.value._1)
        }
      }

    val playerColumns: List[
      javafx.scene.control.TableColumn[(String, String), String]
    ] = controller.getPlayers.map { player =>
      new TableColumn[(String, String), String](player.name) {
        cellValueFactory = { cellData =>
          val category = cellData.value._1
          val score = controller.getPlayers
            .find(_.name == player.name)
            .flatMap(_.scoreCard.categories.get(category))
            .map(p => if p.isDefined then p.get.toString() else "-")
          new StringProperty(
            this,
            "score",
            score.getOrElse("-")
          )
        }
      }
    }

    tableView.columns ++= List(categoryColumn)
    tableView.columns ++= playerColumns

    val allCategories =
      controller.getPlayers.flatMap(_.scoreCard.categories.keys).distinct
    val scoreCardEntries: ObservableBuffer[(String, String)] = ObservableBuffer(allCategories.map { category =>
      category -> controller.getPlayers.head.scoreCard.categories
        .getOrElse(category, "-")
        .toString
    }: _*)

    tableView.items = scoreCardEntries
  }

  def rollDice(): Unit = {
    val keptDiceIndices = diceCheckBoxes.zipWithIndex.collect {
      case (checkBox, index) if checkBox.selected.value => index + 1
    }.toList
    controller.keepDice(keptDiceIndices)
    if (controller.repetitions == 0) {
      rollButton.disable = true
    }
  }

  def updateDiceResults(): Unit = {
    val diceResults = controller.getDice
    diceResultsLabel.text = s"Würfelergebnisse: ${diceResults.mkString(", ")}"
    diceCheckBoxes.zip(diceResults).foreach { case (checkBox, result) =>
      checkBox.text = s"Dice: $result"
    }
  }
}
