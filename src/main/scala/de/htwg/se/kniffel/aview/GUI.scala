package de.htwg.se.kniffel.aview

import de.htwg.se.kniffel.controller.ControllerInterface
import de.htwg.se.kniffel.util.{Observer, KniffelEvent}
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{Pane, VBox, HBox, StackPane}
import scalafx.scene.control.{TableView, TableColumn, Button, Label}
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.application.Platform
import scalafx.stage.Screen
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.image.{Image, ImageView}
import scala.compiletime.uninitialized
import scalafx.Includes.jfxRectangle2D2sfx
import scalafx.scene.control.TextField
import java.nio.file.Paths
import com.google.inject.Inject

class GUI @Inject() (controller: ControllerInterface) extends JFXApp3 with Observer {
  controller.add(this)

  var tableView: TableView[(String, String)] = uninitialized
  var rollButton: Button = uninitialized
  var diceResultsLabel: Label = uninitialized
  var diceImageViews: Seq[ImageView] = uninitialized
  var updateCategoryButton: Button = uninitialized
  var selectedCategory: String = ""
  var playerNameFields: Seq[TextField] = uninitialized
  var selectedDiceIndices: Set[Int] = Set()

  override def update(event: KniffelEvent.Value): Unit = {
    Platform.runLater(event match {
      case KniffelEvent.PrintScoreCard => scorecard()
      case KniffelEvent.PlayerAdded    => scorecard()
      case KniffelEvent.PrintDice      => updateDiceResults()
      case KniffelEvent.NextPlayer     => resetSelectedDice()
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
      val pane = new StackPane()
      playerNameFields = createPlayerNameFields(4)

      val confirmButton = new Button("Confirm")
      confirmButton.onAction = _ => handlePlayerNames()

      val vbox = new VBox(10) {
        children = playerNameFields ++ Seq(confirmButton)
        padding = Insets(20)
        alignment = Pos.Center
      }

      pane.children = vbox
      content = pane
    }
  }

  def mainGameScene(screenBounds: javafx.geometry.Rectangle2D): Scene = {
    new Scene(screenBounds.width, screenBounds.height) {

      val pane = new StackPane()

      tableView = new TableView[(String, String)]()
      tableView.onMouseClicked = _ => handleCategorySelection()

      rollButton = new Button("Roll Dice")
      rollButton.onAction = _ => rollDice()

      updateCategoryButton = new Button("Update Category")
      updateCategoryButton.onAction = _ => updateSelectedCategory()

      diceResultsLabel = new Label("Dice Results: ")

      diceImageViews = createDiceImageViews(5)

      val diceBox = new VBox(10) {
        children = Seq(rollButton, updateCategoryButton, diceResultsLabel) ++ diceImageViews
        padding = Insets(20)
        alignment = Pos.Center
      }

      val hbox = new HBox(20) {
        children = Seq(tableView, diceBox)
        padding = Insets(20)
        alignment = Pos.Center
      }

      val outerVBox = new VBox(20) {
        children = Seq(hbox)
        alignment = Pos.Center
      }

      pane.children = outerVBox
      content = pane

      // Hier wird die Methode updateDiceResults() aufgerufen, um die Würfel zu aktualisieren
      updateDiceResults()
    }
  }

  def createPlayerNameFields(count: Int): Seq[TextField] = {
    (1 to count).map { i =>
      new TextField { promptText = s"Player $i Name" }
    }
  }

  def createDiceImageViews(count: Int): Seq[ImageView] = {
    (1 to count).map { i =>
      new ImageView {
        fitHeight = 50
        fitWidth = 50
        onMouseClicked = _ => toggleDiceSelection(i)
      }
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
    val scoreCardEntries: ObservableBuffer[(String, String)] = ObservableBuffer(
      allCategories.map { category =>
        category -> controller.getPlayers.head.scoreCard.categories
          .getOrElse(category, "-")
          .toString
      }: _*
    )

    tableView.items = scoreCardEntries
  }

  def rollDice(): Unit = {
    val keptDiceIndices = selectedDiceIndices.toList
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

  def toggleDiceSelection(index: Int): Unit = {
    if (selectedDiceIndices.contains(index)) {
      selectedDiceIndices -= index
    } else {
      selectedDiceIndices += index
    }
    updateDiceResults()
  }

  def resetSelectedDice(): Unit = {
    selectedDiceIndices = Set()
    updateDiceResults()
  }

  def getDiceImagePath(diceValue: Int): String = {
    val currentPath = Paths.get(".").toAbsolutePath.normalize().toString
    s"file:///$currentPath/src/main/resources/$diceValue.png"
  }

  def updateDiceResults(): Unit = {
    val diceResults = controller.getDice
    diceResultsLabel.text = s"Dice Results: ${diceResults.mkString(", ")}"
    diceImageViews.zipWithIndex.foreach { case (imageView, index) =>
      val result = diceResults(index)
      imageView.image = new Image(getDiceImagePath(result))
      if (selectedDiceIndices.contains(index + 1)) {
        imageView.fitHeight = 60
        imageView.fitWidth = 60
        imageView.style = "-fx-effect: dropshadow(three-pass-box, rgba(0, 255, 0, 0.8), 10, 0, 0, 0);"
      } else {
        imageView.fitHeight = 50
        imageView.fitWidth = 50
        imageView.style = ""
      }
    }
  }
}