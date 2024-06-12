package de.htwg.se.kniffel.aview

import de.htwg.se.kniffel.controller.Controller
import de.htwg.se.kniffel.util.Observer
import de.htwg.se.kniffel.util.KniffelEvent
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{Pane, VBox, HBox}
import scalafx.scene.control.{TableView, TableColumn, Button, Label, CheckBox}
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.application.Platform
import scalafx.stage.Screen
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.image.{Image, ImageView}
import scala.compiletime.uninitialized
import scalafx.Includes.jfxRectangle2D2sfx
import javafx.stage._
import scalafx.scene.control.TextField

class GUI(controller: Controller) extends JFXApp3 with Observer {
  controller.add(this)

  var tableView: TableView[(String, String)] = uninitialized
  var rollButton: Button = uninitialized
  var diceResultsLabel: Label = uninitialized
  var diceImageViews: Seq[ImageView] = uninitialized
  var diceCheckBoxes: Seq[CheckBox] = uninitialized
  var updateCategoryButton: Button = uninitialized
  var selectedCategory: String = ""
  var playerNameFields: Seq[TextField] = uninitialized

  override def update(event: KniffelEvent.Value): Unit = {
    Platform.runLater(event match {
      case KniffelEvent.PrintScoreCard => scorecard()
      case KniffelEvent.PlayerAdded    => scorecard()
      case KniffelEvent.PrintDice      => updateDiceResults()
      case _                           => println("")
    })
  }

  override def start(): Unit = {
    try {
      val screenBounds = Screen.primary.visualBounds

      stage = new JFXApp3.PrimaryStage {
        title = "Kniffel"
        resizable = true
        scene = playerNameScene(screenBounds)
      }

    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

  def playerNameScene(screenBounds: javafx.geometry.Rectangle2D): Scene = {
    new Scene(screenBounds.width, screenBounds.height) {
      val pane = new Pane()
      playerNameFields = (1 to 4).map { i => new TextField { promptText = s"Player $i Name" } }

      val confirmButton = new Button("Confirm")
      confirmButton.onAction = _ => handlePlayerNames()

      val vbox = new VBox(10) {
        children = playerNameFields :+ confirmButton
        padding = Insets(20)
        alignment = Pos.Center
      }

      pane.children = vbox
      content = pane
    }
  }

  def mainGameScene(screenBounds: javafx.geometry.Rectangle2D): Scene = {
    new Scene(screenBounds.width, screenBounds.height) {

      val pane = new Pane()

      tableView = new TableView[(String, String)]()
      tableView.onMouseClicked = _ => handleCategorySelection()

      rollButton = new Button("Roll Dice")
      rollButton.onAction = _ => rollDice()

      updateCategoryButton = new Button("Update Category")
      updateCategoryButton.onAction = _ => updateSelectedCategory()

      diceResultsLabel = new Label("Dice Results: ")

      diceImageViews = (1 to 5).map { i =>
        new ImageView {
          fitHeight = 50
          fitWidth = 50
        }
      }

      diceCheckBoxes = (1 to 5).map { i =>
        new CheckBox(s"Keep Dice $i")
      }

      val diceBox = new VBox(10) {
        children = Seq(rollButton, updateCategoryButton, diceResultsLabel) ++
          diceImageViews.zip(diceCheckBoxes).flatMap { case (imageView, checkBox) =>
            Seq(imageView, checkBox)
          }
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

  def handlePlayerNames(): Unit = {
    val names = playerNameFields.map(_.text.value.trim).filter(_.nonEmpty)
    names.foreach(controller.addPlayer)
    val screenBounds = Screen.primary.visualBounds
    stage.scene = mainGameScene(screenBounds)
  }

  def handleCategorySelection(): Unit = {
    selectedCategory = tableView.selectionModel().getSelectedItem()._1
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

  def updateSelectedCategory(): Unit = {
    if (selectedCategory.nonEmpty) {
      controller.updateScore(selectedCategory)
      selectedCategory = ""
    }
  }

  def getDiceImagePath(diceValue: Int): String = {
    s"file:///C:/Users/michi/OneDrive/Dokumente/HTWG/SoSe24/SE-Boger/Kniffel/src/main/resources/$diceValue.png"
  }

  def updateDiceResults(): Unit = {
    val diceResults = controller.getDice
    diceResultsLabel.text = s"Dice Results: ${diceResults.mkString(", ")}"
    diceImageViews.zip(diceResults).foreach { case (imageView, result) =>
      imageView.image = new Image(getDiceImagePath(result))
    }
  }
}