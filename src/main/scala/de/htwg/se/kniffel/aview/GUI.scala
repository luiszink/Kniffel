package de.htwg.se.kniffel.aview

import de.htwg.se.kniffel.controller.ControllerInterface
import de.htwg.se.kniffel.util.{Observer, KniffelEvent}
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{Pane, VBox, HBox, StackPane, Priority, BorderPane}
import scalafx.scene.control._
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.application.Platform
import scalafx.stage.Screen
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.image.{Image, ImageView}
import scala.compiletime.uninitialized
import scalafx.Includes.jfxRectangle2D2sfx
import scalafx.scene.control.CheckBox
import scalafx.scene.control.TextField
import java.nio.file.Paths
import com.google.inject.Inject
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import scalafx.scene.layout.AnchorPane

class GUI @Inject() (controller: ControllerInterface)
    extends JFXApp3
    with Observer {
  controller.add(this)

  var tableView: TableView[(String, String)] = uninitialized
  var rollButton: Button = uninitialized
  var diceImageViews: Seq[ImageView] = uninitialized
  var updateCategoryButton: Button = uninitialized
  var selectedCategory: String = ""
  var playerNameFields: Seq[TextField] = uninitialized
  var selectedDiceIndices: Set[Int] = Set()
  var multipleKniffelCheckBox: CheckBox = uninitialized
  var repetitionsLabel: Label = uninitialized

  override def update(event: KniffelEvent.Value): Unit = {
    Platform.runLater {
      event match {
        case KniffelEvent.PrintScoreCard    => scorecard()
        case KniffelEvent.PlayerAdded       => scorecard()
        case KniffelEvent.MultiKniffel      => startSync()
        case KniffelEvent.PrintDice         => updateDiceResults()
        case KniffelEvent.NextPlayer        => resetSelectedDice()
        case KniffelEvent.DisableRollButton => rollButton.disable = true
        case KniffelEvent.EnableRollButton  => rollButton.disable = false
        case _                              => println("")
      }
    }
  }

  override def start(): Unit = {
    try {
      val screenBounds = Screen.primary.visualBounds

      stage = new JFXApp3.PrimaryStage {
        title = "Kniffel"
        resizable = true
        onCloseRequest = _ => {
          controller.saveCurrentState() // Speichern des aktuellen Zustands
          sys.exit(0)
        }
        scene = startScene(screenBounds)
      }

    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

  def startScene(screenBounds: javafx.geometry.Rectangle2D): Scene = {
    new Scene(screenBounds.width, screenBounds.height) {
      stylesheets.add("file:src/main/resources/style.css")

      val pane = new StackPane() {
        id = "main-pane"
        style =
          "-fx-background-image: url('file:src/main/resources/background1.png'); -fx-background-size: cover; -fx-background-position: center;"
        prefWidth = screenBounds.width
        prefHeight = screenBounds.height
      }

      playerNameFields = createPlayerNameFields(4)

      val confirmButton = new Button("Confirm")
      confirmButton.onAction = _ => handlePlayerNames()

      // Checkbox für mehrere Kniffel
      multipleKniffelCheckBox = new CheckBox("Erlaube mehrere Kniffel")
      multipleKniffelCheckBox.selected = false

      // Title Label
      val titleLabel = new Label("Kniffel") {
        style = "-fx-font-size: 36px; -fx-font-weight: bold;"
      }

      val titleBox = new HBox {
        children = Seq(titleLabel)
        alignment = Pos.Center
        padding = Insets(20)
      }

      val playerNamesVBox = new VBox(10) {
        children = playerNameFields
        padding = Insets(20)
        alignment = Pos.Center
      }

      val optionsVBox = new VBox(10) {
        children = Seq(multipleKniffelCheckBox)
        padding = Insets(20)
        alignment = Pos.Center
      }

      val confirmButtonBox = new HBox {
        children = Seq(confirmButton)
        alignment = Pos.Center
        padding = Insets(20)
      }

      val hbox = new HBox(50) {
        children = Seq(playerNamesVBox, optionsVBox)
        alignment = Pos.Center
      }

      val vbox = new VBox(30) {
        children = Seq(titleBox, hbox, confirmButtonBox)
        alignment = Pos.Center
      }

      pane.children = vbox
      content = pane
    }
  }

  def gameScene(screenBounds: javafx.geometry.Rectangle2D): Scene = {
    new Scene(screenBounds.width, screenBounds.height) {
      stylesheets.add("file:src/main/resources/style.css")

      val pane = new BorderPane() {
        id = "main-pane"
        style =
          "-fx-background-image: url('file:src/main/resources/background1.png'); -fx-background-size: cover; -fx-background-position: center;"
        prefWidth = screenBounds.width
        prefHeight = screenBounds.height
      }

      tableView = new TableView[(String, String)]() {
        id = "score-table"
        onMouseClicked = _ => handleCategorySelection()
        prefHeight = 400
        prefWidth = 400
      }

      // Menüleiste mit den drei Strichen und Dropdown-Menü
      val menuBar = new MenuBar()
      val menu = new Menu("≡")
      val undoMenuItem = new MenuItem("Undo")
      val exitMenuItem = new MenuItem("Exit")
      menu.items.addAll(undoMenuItem, exitMenuItem)
      menuBar.menus.add(menu)

      // Event-Handler für Menüeinträge
      undoMenuItem.onAction = _ => {
        controller.handleInput("undo")
        updateDiceResults() // Aktualisiere die Anzeige nach dem Undo
      }
      exitMenuItem.onAction = _ => Platform.exit()

      rollButton = new Button("Roll Dice") {
        id = "roll-button"
        tooltip = "Click to roll the dice"
      }
      rollButton.onAction = _ => rollDice()
      rollButton.disable = false // Ensure the button is enabled initially

      updateCategoryButton = new Button("Update Category") {
        id = "update-category-button"
        tooltip = "Click to update the selected category"
      }
      updateCategoryButton.onAction = _ => updateSelectedCategory()

      diceImageViews = createDiceImageViews(5)

      // Repetitions Label
      repetitionsLabel = new Label(
        s"Remaining Rolls: ${controller.repetitions}"
      ) {
        id = "repetitions-label"
        style = "-fx-font-size: 18px;" // Adjust the font size as needed
      }

      val dicePane = new Pane() {
        prefHeight = 200
        prefWidth = 200
      }

      diceImageViews(0).layoutX = 65
      diceImageViews(0).layoutY = 20

      diceImageViews(1).layoutX = 130
      diceImageViews(1).layoutY = 60

      diceImageViews(2).layoutX = 60
      diceImageViews(2).layoutY = 124

      diceImageViews(3).layoutX = 0
      diceImageViews(3).layoutY = 60

      diceImageViews(4).layoutX = 125
      diceImageViews(4).layoutY = 134

      dicePane.children.addAll(diceImageViews.map(_.delegate)*)

      val controlBox = new VBox(10) {
        children = Seq(
          repetitionsLabel,
          dicePane,
          rollButton,
          updateCategoryButton
        )
        padding = Insets(20)
        alignment = Pos.Center
      }

      val hbox = new HBox(20) {
        children = Seq(tableView, controlBox)
        padding = Insets(20)
        alignment = Pos.Center
      }

      val outerVBox = new VBox(20) {
        children = Seq(hbox)
        alignment = Pos.Center
      }

      pane.top = menuBar
      pane.center = outerVBox
      content = pane

      updateDiceResults()
    }
  }

  def createDiceImageViews(count: Int): Seq[ImageView] = {
    (1 to count).map { i =>
      new ImageView {
        fitHeight = 50
        fitWidth = 50
        onMouseClicked = _ => toggleDiceSelection(i)
        id = s"dice-image-$i"
      }
    }
  }

  def createPlayerNameFields(count: Int): Seq[TextField] = {
    (1 to count).map { i =>
      new TextField {
        promptText = s"Player $i Name"
        id = s"player-$i-name-field"
      }
    }
  }

  def startSync(): Unit = {
    println("startSync GUI")
    val screenBounds = Screen.primary.visualBounds
    stage.scene = gameScene(screenBounds)
    scorecard()
  }

  def handlePlayerNames(): Unit = {
    println("startScene handlePlayerNames 1")
    val names = playerNameFields.map(_.text.value.trim).filter(_.nonEmpty)
    names.foreach(controller.addPlayer)
    val multipleKniffelAllowed = multipleKniffelCheckBox.selected.value
    println("startScene handlePlayerNames 2")
    val scoreUpdaterType = if (multipleKniffelAllowed) "y" else "n"
    println("startScene handlePlayerNames 3")
    controller.setScoreUpdater(scoreUpdaterType)
    println("startScene handlePlayerNames 4")
  }

  def handleCategorySelection(): Unit = {
    selectedCategory = tableView.selectionModel().getSelectedItem()._1
  }

  def scorecard(): Unit = {
    if (tableView != null) {
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
              .map(p => if (p.isDefined) p.get.toString else "-")
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
      val scoreCardEntries: ObservableBuffer[(String, String)] =
        ObservableBuffer(
          allCategories.map { category =>
            category -> controller.getPlayers.head.scoreCard.categories
              .getOrElse(category, "-")
              .toString
          }*
        )

      tableView.items = scoreCardEntries
    }
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
    repetitionsLabel.text = s"Remaining Rolls: ${controller.repetitions}"
    diceImageViews.zipWithIndex.foreach { case (imageView, index) =>
      val result = diceResults(index)
      imageView.image = new Image(getDiceImagePath(result))
      if (selectedDiceIndices.contains(index + 1)) {
        imageView.fitHeight = 60
        imageView.fitWidth = 60
        imageView.style =
          "-fx-effect: dropshadow(three-pass-box, rgba(0, 255, 0, 0.8), 10, 0, 0, 0);"
      } else {
        imageView.fitHeight = 50
        imageView.fitWidth = 50
        imageView.style = ""
      }
    }
  }
}
